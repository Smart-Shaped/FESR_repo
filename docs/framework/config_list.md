# SmartFESR Configuration

## Common Configurations

### Apache Spark

```yml
{layer}.spark.*     # Defines Spark environment configurations (master, deploy-mode, app-name, interval-sec, etc.)
```

### Apache Cassandra

```yml
{layer}.spark.cassandra.connection.host         # Specifies the Cassandra node hostname
{layer}.spark.cassandra.connection.port         # Specifies the Cassandra node connection port
{layer}.cassandra.model.class                   # Specifies the fully qualified class name of the Cassandra model, including package path
{layer}.cassandra.datacenter                    # Defines the reference Cassandra datacenter
{layer}.cassandra.checkpoint                    # Specifies the path for storing Cassandra data checkpoints
{layer}.cassandra.keyspace.name                 # Defines the Cassandra keyspace name (will be created if non-existent)
{layer}.cassandra.keyspace.replication_factor   # Defines the replication factor for the Cassandra keyspace (required for keyspace creation)
```

## ML Layer

```yml
ml.hdfs.readers.default                     # Defines the default path for storing data processed by the reader
ml.hdfs.readers.{reader_identifier}.class   # Specifies the fully qualified class name of the reader, including package path
ml.hdfs.readers.{reader_identifier}.path    # Defines the destination path for storing data processed by the reader
ml.hdfs.modelDir                            # Specifies the storage path for the model when using Pipeline class for machine learning
ml.blackbox.class                           # Specifies the fully qualified class name of the BlackBox (including package). Note: not compatible with pipeline usage
ml.blackbox.folder                          # Defines the folder path for the BlackBox
ml.blackbox.inputs                          # Defines the parameters required for data acquisition by the blackbox
ml.blackbox.output                          # Defines the parameters required for acquiring output produced by the blackbox
ml.blackbox.modelPath                       # Specifies the storage path for the BlackBox model
ml.blackbox.python.scriptPath               # Defines where the main Python script will be copied, or just the name in the resources folder if ml.blackbox.folder is set
ml.blackbox.python.extraScripts             # Lists the paths of additional Python scripts, comma-separated
ml.blackbox.python.libraries                # Lists the Python dependencies required, comma-separated
ml.blackbox.python.requirementsPath         # Defines the path to the requirements.txt file for the Python script
ml.blackbox.python.extraArguments.*         # Defines additional arguments for the Python script
ml.pipeline.class                           # Specifies the fully qualified class name of the pipeline (including package). Note: not compatible with blackbox usage
ml.modelSaver.class                         # Specifies the fully qualified class name of the model saver, including package path
```

## DataFusion Layer

```yml
data_fusion.RequestHandler                                        # Specifies the fully qualified class name of the RequestHandler (including package). Use "default" for standard RequestHandler
data_fusion.downloader.{downloader_class}.params.*                # Defines the configuration parameters for the downloader
data_fusion.downloader.{downloader_class}.url.*                   # Defines the URL-related parameters for the downloader
data_fusion.downloader.{downloader_class}.path.hdfs-path          # Defines the HDFS path for a specified downloader class (BinaryDownloader only)
data_fusion.preprocessor.{preprocessor_class}.params.*            # Defines the configuration parameters for the preprocessor
data_fusion.data_fusions.{data_fusion_identifier}.inputPath           # Specifies the input path for data_fusion data
data_fusion.data_fusions.{data_fusion_identifier}.class               # Specifies the fully qualified class name of the data_fusion, including package path
data_fusion.data_fusions.{data_fusion_identifier}.downloader.class    # Specifies the fully qualified class name of the downloader, including package path
data_fusion.data_fusions.{data_fusion_identifier}.transformer         # Specifies the fully qualified class name of the transformer, including package path
data_fusion.data_fusions.{data_fusion_identifier}.preprocessor        # Specifies the fully qualified class name of the preprocessor, including package path
data_fusion.data_fusions.{data_fusion_identifier}.saver.path          # Defines the destination path for storing data processed by the saver
```
