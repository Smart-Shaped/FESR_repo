import jax
import haiku as hk
import xarray as xr
import numpy as np
import dataclasses
from google.cloud import storage
from graphcast import xarray_jax
from graphcast import normalization
from graphcast import xarray_tree
from graphcast import nan_cleaning
from graphcast import gencast
from graphcast import rollout
from graphcast import checkpoint
from graphcast import data_utils
from graphcast import gencast

class GenCastModel:
    
    dir_prefix = "gencast/"
    bucket_name = "dm_graphcast"
    
    def __init__(self, logger, params_file = "GenCast 1p0deg Mini <2019.npz"):
        self.logger = logger
        self.params_file = params_file
        
        # Google Cloud Storage settings
        gcs_client = storage.Client.create_anonymous_client()
        self.gcs_bucket = gcs_client.get_bucket(self.bucket_name)
    
    def get_model(self):        
        
        # read params file form google cloud
        with self.gcs_bucket.blob(self.dir_prefix + f"params/{self.params_file}").open("rb") as f:
            ckpt = checkpoint.load(f, gencast.CheckPoint)
        
        # load model
        self.params = ckpt.params
        self.state = {}
        self.task_config = ckpt.task_config
        self.sampler_config = ckpt.sampler_config
        self.noise_config = ckpt.noise_config
        self.noise_encoder_config = ckpt.noise_encoder_config
        self.denoiser_architecture_config = ckpt.denoiser_architecture_config
        # extra settings for gpu usage
        self.denoiser_architecture_config.sparse_transformer_config.attention_type = "triblockdiag_mha"
        self.denoiser_architecture_config.sparse_transformer_config.mask_type = "full"
        
        self.logger.info("Model loaded")

class GenCastPipeline:
    
    num_ensemble_members = 1
    
    def __init__(self, logger, model: GenCastModel, diffs_stddev_by_level:xr.DataArray, mean_by_level:xr.Dataset, min_by_level:xr.Dataset, stddev_by_level:xr.Dataset):
        
        self.logger = logger
        self.model = model
        self.diffs_stddev_by_level = diffs_stddev_by_level
        self.mean_by_level = mean_by_level
        self.min_by_level = min_by_level
        self.stddev_by_level = stddev_by_level
        
        self.params = self.model.params
        self.state = {}
        self.task_config = self.model.task_config
        self.sampler_config = self.model.sampler_config
        self.noise_config = self.model.noise_config
        self.noise_encoder_config = self.model.noise_encoder_config
        self.denoiser_architecture_config = self.model.denoiser_architecture_config
        # extra settings for gpu usage
        self.denoiser_architecture_config.sparse_transformer_config.attention_type = "triblockdiag_mha"
        self.denoiser_architecture_config.sparse_transformer_config.mask_type = "full"
        
    def train_test_split(self, example_batch: xr.Dataset):
        # train test split
        train_inputs, train_targets, train_forcings = data_utils.extract_inputs_targets_forcings(
            example_batch, target_lead_times=slice("12h", "12h"), # Only 1AR training.
            **dataclasses.asdict(self.task_config))

        eval_inputs, eval_targets, eval_forcings = data_utils.extract_inputs_targets_forcings(
            example_batch, target_lead_times=slice("12h", f"{(example_batch.dims['time']-2)*12}h"), # All but 2 input frames.
            **dataclasses.asdict(self.task_config))

        self.logger.info("Train test split complete")
        self.logger.debug(f"All Examples: {example_batch.dims.mapping}")
        self.logger.debug(f"Train Inputs: {train_inputs.dims.mapping}")
        self.logger.debug(f"Train Targets: {train_targets.dims.mapping}")
        self.logger.debug(f"Train Forcings: {train_forcings.dims.mapping}")
        self.logger.debug(f"Eval Inputs: {eval_inputs.dims.mapping}")
        self.logger.debug(f"Eval Targets: {eval_targets.dims.mapping}")
        self.logger.debug(f"Eval Forcings: {eval_forcings.dims.mapping}")
        
        self.train_inputs = train_inputs
        self.train_targets = train_targets
        self.train_forcings = train_forcings
        self.eval_inputs = eval_inputs
        self.eval_targets = eval_targets
        self.eval_forcings = eval_forcings
        
    def build_jitted_functions(self):
        def construct_wrapped_gencast():
            """Constructs and wraps the GenCast Predictor."""
            predictor = gencast.GenCast(
                sampler_config=self.sampler_config,
                task_config=self.task_config,
                denoiser_architecture_config=self.denoiser_architecture_config,
                noise_config=self.noise_config,
                noise_encoder_config=self.noise_encoder_config,
            )

            predictor = normalization.InputsAndResiduals(
                predictor,
                diffs_stddev_by_level=self.diffs_stddev_by_level,
                mean_by_level=self.mean_by_level,
                stddev_by_level=self.stddev_by_level,
            )

            predictor = nan_cleaning.NaNCleaner(
                predictor=predictor,
                reintroduce_nans=True,
                fill_value=self.min_by_level,
                var_to_clean='sea_surface_temperature',
            )

            return predictor

        @hk.transform_with_state
        def run_forward(inputs, targets_template, forcings):
            predictor = construct_wrapped_gencast()
            return predictor(inputs, targets_template=targets_template, forcings=forcings)

        @hk.transform_with_state
        def loss_fn(inputs, targets, forcings):
            predictor = construct_wrapped_gencast()
            loss, diagnostics = predictor.loss(inputs, targets, forcings)
            return xarray_tree.map_structure(
                lambda x: xarray_jax.unwrap_data(x.mean(), require_jax=True),
                (loss, diagnostics),
            )

        def grads_fn(params, state, inputs, targets, forcings):
            def _aux(params, state, i, t, f):
                (loss, diagnostics), next_state = loss_fn.apply(
                    params, state, jax.random.PRNGKey(0), i, t, f
                )
                return loss, (diagnostics, next_state)

            (loss, (diagnostics, next_state)), grads = jax.value_and_grad(
                _aux, has_aux=True
            )(params, state, inputs, targets, forcings)
            return loss, diagnostics, next_state, grads

        if self.params is None:
            init_jitted = jax.jit(loss_fn.init)
            self.params, self.state = init_jitted(
                rng=jax.random.PRNGKey(0),
                inputs=self.train_inputs,
                targets=self.train_targets,
                forcings=self.train_forcings,
            )

        self.loss_fn_jitted = jax.jit(
            lambda rng, i, t, f: loss_fn.apply(self.params, self.state, rng, i, t, f)[0]
        )
        self.grads_fn_jitted = jax.jit(grads_fn)
        run_forward_jitted = jax.jit(
            lambda rng, i, t, f: run_forward.apply(self.params, self.state, rng, i, t, f)[0]
        )
        # We also produce a pmapped version for running in parallel.
        self.run_forward_pmap = xarray_jax.pmap(run_forward_jitted, dim="sample")    
    
    def inference(self):
        self.logger.info("Starting inference from pretrained model")
        
        # prediction with pretrained model
        
        rng = jax.random.PRNGKey(0)
        # We fold-in the ensemble member, this way the first N members should always
        # match across different runs which use take the same inputs, regardless of
        # total ensemble size.
        rngs = np.stack(
            [jax.random.fold_in(rng, i) for i in range(self.num_ensemble_members)], axis=0)

        chunks = []
        for chunk in rollout.chunked_prediction_generator_multiple_runs(
            # Use pmapped version to parallelise across devices.
            predictor_fn=self.run_forward_pmap,
            rngs=rngs,
            inputs=self.eval_inputs,
            targets_template=self.eval_targets * np.nan,
            forcings=self.eval_forcings,
            num_steps_per_chunk = 1,
            num_samples = self.num_ensemble_members,
            pmap_devices=jax.local_devices()
            ):
            chunks.append(chunk)
        predictions = xr.combine_by_coords(chunks)
        
        self.logger.info("Inference complete")
        
        return predictions
