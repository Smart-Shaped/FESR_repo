package com.smartshaped.smartfesr.datafusion.utils;

import com.smartshaped.smartfesr.common.exception.CassandraException;
import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.common.utils.ConfigurationUtils;
import com.smartshaped.smartfesr.datafusion.DataFusion;
import com.smartshaped.smartfesr.datafusion.downloader.Downloader;
import com.smartshaped.smartfesr.datafusion.request.RequestHandler;
import com.smartshaped.smartfesr.preprocessing.Preprocessor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class that extends {@link ConfigurationUtils} for reading configuration files related to
 * DataFusion layer.
 */
public class DataFusionConfigurationUtils extends ConfigurationUtils {

  private static final Logger logger = LogManager.getLogger(DataFusionConfigurationUtils.class);

  private static final String SEPARATOR = ".";
  private static final String ROOT = "data_fusion";
  private static final String CLASS = "class";
  private static final String PATH = "path";
  private static final String SAVER = "saver";
  private static final String PREPROCESSOR = "preprocessor";
  private static final String DOWNLOADER = "downloader";
  private static final String DATA_FUSIONS = "data_fusion.data_fusions";
  private static final String URL = "url";
  private static final String PARAMS = "params";
  private static final String DUE_TO_EXCEPTION = " due to exception";
  private static final String COULD_NOT_INSTANTIATE = "Could not instantiate ";
  private static final String INPUT_PATH = "inputPath";
  private static final String HDFS_PATH = "hdfs-path";

  private static DataFusionConfigurationUtils configuration;

  /**
   * Utility class that extends {@link ConfigurationUtils} for reading configuration files related
   * to DataFusion layer.
   *
   * <p>This class provides methods to retrieve various configurations such as input paths, output
   * paths, preprocessors, and downloaders for different data_fusions. It also supports loading
   * instances of classes based on configuration settings.
   *
   * <p>This class follows the singleton pattern to ensure a single configuration instance is used
   * throughout the application.
   */
  private DataFusionConfigurationUtils() throws ConfigurationException {
    super();
    this.setConfRoot(ROOT.concat(SEPARATOR));
  }

  /**
   * Gets the instance of the DataFusionConfigurationUtils class, which is responsible for loading
   * the configurations for the DataFusion layer.
   *
   * <p>If the configuration is not yet loaded, it will be loaded and stored in the static variable.
   *
   * <p>
   *
   * @return the instance of the DataFusionConfigurationUtils class
   * @throws ConfigurationException if there is an error loading the configurations
   */
  public static DataFusionConfigurationUtils getDataFusionConf() throws ConfigurationException {
    logger.debug("Loading data_fusion configuration");
    if (configuration == null) {
      logger.debug("No previous data_fusion configuration found, loading new configurations");
      configuration = new DataFusionConfigurationUtils();
    }
    return configuration;
  }

  /**
   * Gets the data_fusion ID for the given class name.
   *
   * <p>This method iterates over the list of data_fusions in the configuration and returns the
   * data_fusion ID for the class name that matches the given argument.
   *
   * <p>
   *
   * @param className the class name to get the data_fusion ID for
   * @return the data_fusion ID
   * @throws ConfigurationException if no data_fusion ID is found for the given class name
   */
  public String getDataFusionId(String className) throws ConfigurationException {
    logger.debug("Retrieving data_fusion ID for class: {}", className);
    List<HierarchicalConfiguration<ImmutableNode>> dataFusionList =
        config.childConfigurationsAt(DATA_FUSIONS);
    for (HierarchicalConfiguration<ImmutableNode> dataFusionNode : dataFusionList) {
      String nodeClassName = dataFusionNode.getString(CLASS);
      if (className.equals(nodeClassName)) {
        return DATA_FUSIONS + SEPARATOR + dataFusionNode.getRootElementName();
      }
    }
    throw new ConfigurationException("No data_fusion ID found for class: " + className);
  }

  /**
   * Returns a list of instances of the DataFusion class, based on the data_fusions listed in the
   * configuration.
   *
   * <p>The method iterates over the list of data_fusions in the configuration and instantiates each
   * one using the {@link #loadInstanceOf(String, Class)} method. The instantiated data_fusions are
   * then returned as a list.
   *
   * <p>
   *
   * @return the list of data_fusions
   * @throws ConfigurationException if no data_fusions are defined in the configuration or if there
   *     is an error while instantiating a data_fusion
   */
  public List<DataFusion> getDataFusions() throws ConfigurationException {
    List<DataFusion> dataFusions = new LinkedList<>();
    Iterator<String> keys = config.getKeys(DATA_FUSIONS);
    String fullKey;
    String dataFusionClassName;
    String[] segments;

    logger.debug("Retrieving data_fusions...");

    while (keys.hasNext()) {
      fullKey = keys.next();
      segments = fullKey.split("\\.");
      if (segments.length == 4 && "data_fusions".equals(segments[1]) && CLASS.equals(segments[3])) {
        dataFusionClassName = config.getString(fullKey);
        if (dataFusionClassName == null || dataFusionClassName.trim().isEmpty()) {
          throw new ConfigurationException(
              "At least a data_fusion must be defined in data_fusion.data_fusions config");
        }
        try {
          dataFusions.add(loadInstanceOf(dataFusionClassName, DataFusion.class));
          logger.debug("DataFusion class '{}' succesfully loaded", dataFusionClassName);
        } catch (ConfigurationException e) {
          throw new ConfigurationException(
              COULD_NOT_INSTANTIATE + DataFusion.class.toString() + DUE_TO_EXCEPTION, e);
        }
      }
    }

    return dataFusions;
  }

  /**
   * Retrieves an instance of the RequestHandler class based on the configuration.
   *
   * <p>The method attempts to fetch the RequestHandler class name from the configuration, and if it
   * is set to "default", it returns a new instance of the default RequestHandler. Otherwise, it
   * tries to instantiate the specified RequestHandler class.
   *
   * <p>
   *
   * @return an instance of RequestHandler
   * @throws ConfigurationException if no RequestHandler is defined in the configurations or if
   *     there is an error during instantiation
   * @throws CassandraException if there is an error related to Cassandra operations
   */
  public RequestHandler getRequestHandler() throws ConfigurationException, CassandraException {
    String handlerName = config.getString(ROOT.concat(SEPARATOR) + "RequestHandler");
    logger.debug("Loading RequestHandler: {}", handlerName);

    if (handlerName == null) {
      throw new ConfigurationException(
          "No RequestHandler defined in configurations in data_fusion.RequestHandler");
    }
    if (handlerName.equals("default")) {
      return new RequestHandler();
    }
    try {
      return loadInstanceOf(handlerName, RequestHandler.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          COULD_NOT_INSTANTIATE + RequestHandler.class + DUE_TO_EXCEPTION, e);
    }
  }

  /**
   * Retrieves a map of preprocessor parameters for the specified class.
   *
   * <p>This method constructs the base path for the preprocessor parameters using the provided
   * class name and iteratively fetches each key-value pair from the configuration. If a key
   * contains a period, only the segment after the last period is used as the final key in the map.
   *
   * <p>
   *
   * @param className the name of the class for which preprocessor parameters are being retrieved.
   * @return a map where keys are parameter names and values are the corresponding parameter values.
   * @throws RuntimeException if there is an error retrieving any parameter value.
   */
  public Map<String, String> getPreprocessorParams(String className) throws ConfigurationException {
    logger.debug("Starting to retrieve preprocessor parameters for class: {}", className);

    Map<String, String> params = new HashMap<>();
    String basePath = confRoot + PREPROCESSOR + SEPARATOR + className + SEPARATOR + PARAMS;
    logger.debug("Base path for preprocessor keys: {}", basePath);

    Iterator<String> iterator = config.getKeys(basePath);
    if (!iterator.hasNext()) {
      logger.warn("No keys found under base path: {}", basePath);
    }

    while (iterator.hasNext()) {
      String key = iterator.next();
      logger.debug("Processing key: {}", key);

      try {
        String originalKey = key;
        int lastDotIndex = key.lastIndexOf(SEPARATOR);
        if (lastDotIndex != -1) {
          key = key.substring(lastDotIndex + 1);
        }
        String value = config.getString(originalKey);
        logger.debug("Retrieved value for key '{}': {}", originalKey, value);
        params.put(key, value);
      } catch (Exception e) {
        throw new ConfigurationException("Failed to retrieve value for key: " + key, e);
      }
    }

    logger.debug(
        "Finished retrieving preprocesssor parameters for class: {}. Total parameters: {}",
        className,
        params.size());

    logger.info("Finished retrieving preprocessor parameters");
    return params;
  }

  /**
   * Retrieves a preprocessor instance based on the preprocessor name configured for the provided
   * data_fusion ID.
   *
   * <p>If no preprocessor is specified in the configurations for the given data_fusion ID, an empty
   * preprocessor is used.
   *
   * <p>
   *
   * @param dataFusionId the ID of the data_fusion for which the preprocessor is being retrieved.
   * @return an instance of the preprocessor class configured for the given data_fusion ID.
   * @throws ConfigurationException if there is an error while loading the preprocessor instance.
   */
  public Preprocessor getPreprocessor(String dataFusionId) throws ConfigurationException {
    String preprocessorName = config.getString(dataFusionId + SEPARATOR.concat(PREPROCESSOR), "");

    if (preprocessorName.trim().isEmpty()) {
      logger.warn("No preprocessor specified in the configurations, using empty one...");
      return null;
    }

    try {
      return loadInstanceOf(preprocessorName, Preprocessor.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          COULD_NOT_INSTANTIATE + RequestHandler.class + DUE_TO_EXCEPTION, e);
    }
  }

  /**
   * Retrieves a map of url parameters for the specified class.
   *
   * <p>The method constructs the base path for the url parameters using the provided class name and
   * iteratively fetches each key-value pair from the configuration. If a key contains a period,
   * only the segment after the last period is used as the final key in the map.
   *
   * <p>
   *
   * @param className the name of the class for which url parameters are being retrieved.
   * @return a map where keys are parameter names and values are the corresponding parameter values.
   * @throws ConfigurationException if there is an error retrieving any parameter value.
   */
  public Map<String, String> getUrlParams(String className) throws ConfigurationException {
    logger.debug("Starting to retrieve url parameters for class: {}", className);

    String basePath = confRoot + DOWNLOADER + SEPARATOR + className + SEPARATOR + URL;
    logger.debug("Base path for configuration keys: {}", basePath);

    return getParams(basePath);
  }

  /**
   * Retrieves a map of query parameters for the specified class.
   *
   * <p>The method constructs the base path for the query parameters using the provided class name
   * and iteratively fetches each key-value pair from the configuration. If a key contains a period,
   * only the segment after the last period is used as the final key in the map.
   *
   * <p>
   *
   * @param className the name of the class for which query parameters are being retrieved.
   * @return a map where keys are parameter names and values are the corresponding parameter values.
   * @throws ConfigurationException if there is an error retrieving any parameter value.
   */
  public Map<String, String> getQueryParam(String className) throws ConfigurationException {
    logger.debug("Starting to retrieve query parameters for class: {}", className);
    String basePath = confRoot + DOWNLOADER + SEPARATOR + className + SEPARATOR + PARAMS;
    logger.debug("Base path for configuration keys: {}", basePath);

    return getParams(basePath);
  }

  /**
   * Retrieves a map of parameters for a specified base path in the configuration.
   *
   * <p>This method iterates over the configuration keys under the given base path and constructs a
   * map where each key is the segment after the last period in the original key, and the value is
   * the corresponding configuration value.
   *
   * <p>
   *
   * @param basePath the base path in the configuration from which parameters are retrieved.
   * @return a map where keys are parameter names and values are the corresponding parameter values.
   * @throws ConfigurationException if there is an error retrieving any parameter value.
   */
  private Map<String, String> getParams(String basePath) throws ConfigurationException {
    Map<String, String> params = new HashMap<>();

    Iterator<String> iterator = config.getKeys(basePath);
    if (!iterator.hasNext()) {
      logger.warn("No keys found under base path: {}", basePath);
    }

    while (iterator.hasNext()) {
      String key = iterator.next();
      logger.debug("Processing key: {}", key);
      try {
        String originalKey = key;
        int lastDotIndex = key.lastIndexOf(SEPARATOR);
        if (lastDotIndex != -1) {
          key = key.substring(lastDotIndex + 1);
        }
        String value = config.getString(originalKey);
        logger.debug("Retrieved value for key '{}': {}", originalKey, value);
        params.put(key, value);
      } catch (Exception e) {
        throw new ConfigurationException("Failed to retrieve value for key: " + key, e);
      }
    }

    logger.info("Finished retrieving parameters");
    logger.debug("Total parameters: {}", params.size());

    return params;
  }

  /**
   * Retrieves a Downloader instance based on the downloader name configured for the given
   * data_fusion ID.
   *
   * <p>This method fetches the downloader class name from the configuration using the provided
   * data_fusion ID. If no class name is specified, it throws a ConfigurationException. Otherwise,
   * it attempts to load and instantiate the downloader class.
   *
   * <p>
   *
   * @param dataFusionId the ID of the data_fusion for which the downloader is being retrieved.
   * @return an instance of the configured Downloader class.
   * @throws ConfigurationException if no downloader is specified in the configurations or if there
   *     is an error during instantiation.
   */
  public Downloader getDownloader(String dataFusionId) throws ConfigurationException {
    logger.debug("Attempting to retrieve downloader name ");
    logger.debug("DataFusion ID: {}", dataFusionId);
    String downloaderName =
        config.getString(
            dataFusionId + SEPARATOR.concat(DOWNLOADER.concat(SEPARATOR.concat(CLASS))));

    if (downloaderName == null) {
      throw new ConfigurationException("No downloader specified in the configurations.");
    }

    try {
      logger.debug("Loading instance of downloader: {}", downloaderName);
      return loadInstanceOf(downloaderName, Downloader.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          COULD_NOT_INSTANTIATE + Downloader.class + DUE_TO_EXCEPTION, e);
    }
  }

  /**
   * Retrieves the HDFS path for a specified downloader class.
   *
   * <p>This method constructs the HDFS path using the provided class name by appending it to the
   * base path defined in the configuration. If the constructed path is null, a
   * ConfigurationException is thrown.
   *
   * <p>
   *
   * @param className the name of the downloader class for which the HDFS path is being retrieved.
   * @return the HDFS path for the specified downloader class.
   * @throws ConfigurationException if no HDFS path is specified in the configurations.
   */
  public String getDownloaderHdfsPath(String className) throws ConfigurationException {
    logger.debug("Attempting to retrieve downloader hdfs path String ");
    logger.debug("Downloader : {}", className);

    return config.getString(
        confRoot + DOWNLOADER + SEPARATOR + className + SEPARATOR + PATH + SEPARATOR + HDFS_PATH);
  }

  /**
   * Retrieves the output path configured for the given data_fusion ID.
   *
   * <p>This method fetches the output path from the configuration using the provided data_fusion
   * ID. If no output path is specified in the configurations, an empty string is returned.
   *
   * <p>
   *
   * @param dataFusionId the ID of the data_fusion for which the output path is being retrieved.
   * @return the output path configured for the given data_fusion ID.
   */
  public String getOutputPath(String dataFusionId) {
    return config.getString(
        dataFusionId + SEPARATOR.concat(SAVER.concat(SEPARATOR.concat(PATH))), "");
  }

  /**
   * Retrieves the input path configured for the given data_fusion ID.
   *
   * <p>This method fetches the input path from the configuration using the provided data_fusion ID.
   * If no input path is specified in the configurations, an empty string is returned.
   *
   * <p>
   *
   * @param dataFusionId the ID of the data_fusion for which the input path is being retrieved.
   * @return the input path configured for the given data_fusion ID.
   */
  public String getInputPath(String dataFusionId) {
    String path = dataFusionId + SEPARATOR + INPUT_PATH;
    logger.debug("Input path: {}", path);
    return config.getString(path, "");
  }
}
