import sys
import re
import json
import pandas as pd
import xarray as xr
from datetime import datetime, timedelta
from pyspark.sql import SparkSession, functions
from gencast_pipeline import GenCastPipeline, GenCastModel
from blackbox_methods import get_logger, get_spark, read_binary_from_hdfs, get_standard_inputs

def get_extra_inputs():
    """
    Retrieves the model version and bounding box from the command-line arguments.

    This function reads the model version and bounding box (bbox) from command-line
    arguments, converting the bounding box from a JSON string to a Python object.

    Returns
    -------
    tuple
        A tuple containing:
        - model_version (str): The version of the model.
        - bbox (dict): The bounding box as a dictionary.

    Raises
    ------
    TypeError
        If either the model_version or bbox is not provided in the arguments.
    """

    args = sys.argv
    model_version = args[4]
    bbox = json.loads(args[5])
    
    if (model_version is None or bbox is None):
        raise TypeError("Missing arguments")
    
    return (model_version, bbox)

def read_data(logger, hdfs_path):
    """
    Reads a binary file from HDFS and returns it as an xarray dataset.

    This function reads a binary file from HDFS using the read_binary_from_hdfs
    function and returns it as an xarray dataset.
    """
    
    return read_binary_from_hdfs(logger=logger,
                                 url='http://namenode:9870', 
                                 hdfs_path=hdfs_path,
                                 user='spark',
                                 extension='.nc',
                                 read_function=xr.open_dataset)

def extract_date(input_string):
    """
    Extracts the date from the input string in the format 'date-YYYY-MM-DD_res-'

    This function takes an input string and searches for a pattern of the form
    'date-YYYY-MM-DD_res-' using a regular expression. If the pattern is found,
    the function returns the date string (YYYY-MM-DD). If the pattern is not
    found, the function raises a ValueError.

    Parameters
    ----------
    input_string : str
        The input string to search.

    Returns
    -------
    str
        The date string (YYYY-MM-DD) if found, otherwise raises a ValueError.

    Raises
    ------
    ValueError
        If the date is not found in the input string.
    """
    
    pattern = r'date-(\d{4}-\d{2}-\d{2})_res-'
    match = re.search(pattern, input_string)
    
    if match:
        date_string = match.group(1)
        return date_string
    else:
        raise ValueError("Date not found in input string")

def read_datasets(logger, inputs):
    """
    Reads multiple datasets from HDFS and returns them as separate xarray datasets.

    This function reads multiple datasets from HDFS using the read_binary_from_hdfs
    function and returns them as separate xarray datasets.

    Parameters
    ----------
    logger : Logger
        The logger to use for logging.
    inputs : str
        A comma-separated string of the paths to the input datasets.

    Returns
    -------
    tuple
        A tuple containing:
        - example_batch (xarray.Dataset): The example batch dataset.
        - diffs_stddev_by_level (xarray.DataArray): The differences standard deviation by level dataset.
        - mean_by_level (xarray.Dataset): The mean by level dataset.
        - min_by_level (xarray.Dataset): The minimum by level dataset.
        - stddev_by_level (xarray.Dataset): The standard deviation by level dataset.
        - start_date (str): The start date of the example batch dataset in the format 'YYYY-MM-DD'.
    """
    
    logger.info("Reading inputs from HDFS")
    dss = {}
    for input in inputs.split(","):
        dss[input] = read_data(logger, input)
    
    # extract correct file names for datasets
    keys = list(dss.keys())
    dataset_keys = [key for key in keys if "/dataset/" in key]
    diffs_stddev_by_level_keys = [key for key in keys if "/stats/diffs_stddev_by_level.nc" in key]
    mean_by_level_keys = [key for key in keys if "/stats/mean_by_level.nc" in key]
    min_by_level_keys = [key for key in keys if "/stats/min_by_level.nc" in key]
    stddev_by_level_keys = [key for key in keys if "/stats/stddev_by_level.nc" in key]
    assert len(dataset_keys) == 1
    assert len(diffs_stddev_by_level_keys) == 1
    assert len(mean_by_level_keys) == 1
    assert len(min_by_level_keys) == 1
    assert len(dataset_keys) == 1
    
    example_batch = dss[dataset_keys[0]]
    diffs_stddev_by_level = dss[diffs_stddev_by_level_keys[0]]
    mean_by_level = dss[mean_by_level_keys[0]]
    min_by_level = dss[min_by_level_keys[0]]
    stddev_by_level = dss[stddev_by_level_keys[0]]
    
    logger.info("Reading inputs complete")
    
    logger.info("Extracting start date")
    start_date = extract_date(dataset_keys[0])
    logger.debug(f"Start date: {start_date}")
    
    return example_batch, diffs_stddev_by_level, mean_by_level, min_by_level, stddev_by_level, start_date

def process_dataset(logger, dataset: xr.Dataset, bbox: dict, start_date: str) -> pd.DataFrame:
    """
    Process an xarray dataset to a pandas dataframe by:
    - converting it to a dataframe
    - dropping unneeded columns
    - renaming columns with names that starts with numbers
    - filtering by bounding box
    - calculating datetime from start date and timedelta

    Parameters
    ----------
    logger : Logger
        The logger to use for logging.
    dataset : xr.Dataset
        The xarray dataset to process.
    bbox : dict
        The bounding box to filter by. If None, no filtering is done.
    start_date : str
        The start date to use for calculating datetime.

    Returns
    -------
    pd.DataFrame
        The processed pandas dataframe.
    """
    
    logger.info("Converting xarray dataset to pandas dataframe")
    df = dataset.to_dataframe().reset_index()
    
    logger.info("Show dataframe information")
    df.info()
    
    # remove unneeded columns
    df = df.drop(['batch'], axis=1)
    if 'sample' in df.columns:
        df = df.drop(['sample'], axis=1)
    # rename columns with names that starts with numbers
    df = df.rename({'10m_u_component_of_wind':'u_component_of_wind_10m', '10m_v_component_of_wind':'v_component_of_wind_10m', '2m_temperature':'temperature_2m'}, axis=1)
    
    # filter by bounding box
    if bbox is not None and type(bbox) is dict:
        df = df[df.lon.between(bbox['min_lon'], bbox['max_lon'])]
        df = df[df.lat.between(bbox['min_lat'], bbox['max_lat'])]
    
    # calculate datetime from start date and timedelta
    df['datetime'] = pd.to_datetime(start_date)
    df['time'] = pd.to_timedelta(df['time'],'ns')
    df['datetime'] = df["time"] + df['datetime']
    del df['time']
    
    logger.debug(df.shape)
    
    return df

def save_predictions(logger, predictions: xr.Dataset, example_batch: xr.Dataset, spark: SparkSession, output_detail: str, bbox: dict, start_date: str):
    """
    Save predictions to a temporary view.

    Parameters
    ----------
    logger : Logger
        The logger to use for logging.
    predictions : xr.Dataset
        The xarray dataset of predictions.
    example_batch : xr.Dataset
        The xarray dataset of example batch.
    spark : SparkSession
        The Spark session to use for saving the parquet file.
    output_detail : str
        The detail of the output.
    bbox : dict
        The bounding box to filter by. If None, no filtering is done.
    start_date : str
        The start date to use for calculating datetime.
    """
    
    logger.info("Processing predictions dataset")
    predictions_df = process_dataset(logger, predictions, bbox, start_date)
    
    logger.info("Processing example batch dataset")
    example_batch_df = process_dataset(logger, example_batch, bbox, start_date)
    
    logger.info("Difference between predictions and example batch")
    
    # only keep prediction columns
    example_batch_df = example_batch_df[list(set(predictions_df.columns) & set(example_batch_df.columns))]
    # only keep values of datetime common to both datasets
    example_batch_df = example_batch_df[example_batch_df['datetime'].isin(predictions_df['datetime'])]
    assert set(predictions_df.columns) == set(example_batch_df.columns)
    assert predictions_df.shape == example_batch_df.shape
    
    # calculate a new field difference for every field
    exclude_fields = ['datetime', 'lat', 'lon', 'level']
    predictions_df = predictions_df.set_index(exclude_fields).sort_index()
    example_batch_df = example_batch_df.set_index(exclude_fields).sort_index()
    
    df = predictions_df.copy()
    
    for field in df.columns:
        df[field + '_error'] = predictions_df[field] - example_batch_df[field]
        
    df = df.reset_index()
    
    logger.info("Converting predictions to Spark DataFrame")
    
    df = spark.createDataFrame(df).repartition(100)
    
    logger.info("Show Spark DataFrame information")
    df.printSchema()
    
    logger.debug(df.count())
    
    logger.info("Registering Spark DataFrame as temporary SQL view")
    df.createOrReplaceTempView(output_detail)

if __name__ == "__main__":

    # get inputs from command args
    (inputs, output, model_path) = get_standard_inputs()
    (model_version, bbox) = get_extra_inputs()
    # get spark session
    spark = get_spark()
    logger = get_logger(spark=spark)
    
    logger.debug(f"Script inputs: {inputs}")
    logger.debug(f"Script output: {output}")
    logger.debug(f"Script model_path: {model_path}")
    logger.debug(f"Script model_version: {model_version}")
    logger.debug(f"Script bbox: {bbox}")
    
    # read data from provided inputs
    example_batch, diffs_stddev_by_level, mean_by_level, min_by_level, stddev_by_level, start_date = read_datasets(logger, inputs)
    
    # create gencast model instance
    gencast_model = GenCastModel(logger, model_version)
    gencast_model.get_model()
    
    # create gencast pipeline instance
    gencast_instance = GenCastPipeline(logger, gencast_model, diffs_stddev_by_level, mean_by_level, min_by_level, stddev_by_level)
    gencast_instance.train_test_split(example_batch)
    gencast_instance.build_jitted_functions()
    predictions = gencast_instance.inference()
    
    # save predictions
    save_predictions(logger, predictions, example_batch, spark, output, bbox, start_date)
    
    logger.info("Blackbox script complete")
    
    sys.exit(0)
    