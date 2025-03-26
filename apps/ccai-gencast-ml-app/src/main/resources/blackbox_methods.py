import os
import sys
import hdfs
import tempfile
from pyspark import java_gateway, SparkContext
from pyspark.conf import SparkConf
from pyspark.sql import SparkSession

def get_standard_inputs():
    """
    Retrieves the input paths, output path, and model path from the command-line arguments.

    Retrieves the input paths from the command-line arguments, parses them into a comma-separated
    string, retrieves the output path and model path from the command-line arguments, logs the
    retrieved paths, and returns the retrieved paths.

    Returns
    -------

    - inputs (str): A comma-separated string of the input paths.
    - output (str): The output path.
    - model_path (str): The model path.
    """
    
    args = sys.argv

    inputs = args[1]
    output = args[2]
    model_path = args[3]

    if (inputs is None or output is None or model_path is None):
        raise TypeError("Missing arguments")
    
    return inputs, output, model_path

def get_spark(): 
    """
    Initializes and returns a SparkSession using a custom Java gateway and Spark configuration.
    
    The function launches a Java gateway to retrieve the Java SparkContext, constructs the
    SparkConf using the gateway and SparkContext configuration, and creates a new SparkContext.
    Logs the retrieval of the Spark session and returns the SparkSession object.
    
    Returns
    -------
    SparkSession
        The initialized Spark session.
    """

    gateway = java_gateway.launch_gateway()
    jsc = gateway.jvm.com.smartshaped.smartfesr.gencast.ml.GenCastBlackbox.javaSparkContext
    conf = SparkConf(True, gateway.jvm, jsc.getConf())

    sc = SparkContext(gateway=gateway, jsc=jsc, conf=conf)
    
    return SparkSession(sc)

def get_logger(spark: SparkSession):
    """
    Retrieves a logger instance from the Spark session.

    This function accesses the Log4j logger from the provided Spark session's
    Java Virtual Machine (JVM) instance and retrieves a logger specific to
    the current module.

    Parameters
    ----------
    spark : SparkSession
        The Spark session from which to access the JVM and retrieve the logger.

    Returns
    -------
    Logger
        The Log4j logger instance for the current module.
    """

    log4j_logger = spark._jvm.org.apache.log4j
    return log4j_logger.LogManager.getLogger(__name__)

def read_binary_from_hdfs(logger, hdfs_path: str, url: str, user: str, extension: str, read_function):
    """
    Reads a binary file from HDFS into any object using a user-provided function.

    Given a path to a file in HDFS, this function reads the file into a temporary
    file, reads the temporary file into an object using the provided
    read_function, and returns the object.

    Parameters
    ----------
    logger : Logger
        The logger to use for logging.
    hdfs_path : str
        The path to the file in HDFS to read.
    url : str
        The URL of the HDFS namenode.
    user : str
        The username to use when connecting to HDFS.
    extension : str
        The file extension of the temporary file to create.
    read_function : callable
        A function taking a single argument (the path to a file) that reads the
        file into an object.

    Returns
    -------
    Any
        The object read from the file in HDFS.
    """
    
    logger.debug(f"Reading data from HDFS: {hdfs_path}")
    
    client = hdfs.InsecureClient(url, user=user)
    
    with tempfile.NamedTemporaryFile(suffix=extension, delete=False) as tmp_file:
        local_path = tmp_file.name

        with client.read(hdfs_path) as reader:
            with open(local_path, 'wb') as writer:
                writer.write(reader.read())
                
        dataset = read_function(local_path)

        os.unlink(local_path)
        
        logger.debug(f"Read data from {local_path} and then delete it")
        
    return dataset
