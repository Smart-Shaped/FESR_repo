package com.smartshaped.smart_fesr.data_fusion;

import com.smartshaped.smart_fesr.common.exception.CassandraException;
import com.smartshaped.smart_fesr.common.exception.ConfigurationException;
import com.smartshaped.smart_fesr.data_fusion.exception.DataFusionException;
import com.smartshaped.smart_fesr.data_fusion.exception.DataFusionLayerException;
import com.smartshaped.smart_fesr.data_fusion.exception.DownloaderException;
import com.smartshaped.smart_fesr.data_fusion.request.Request;
import com.smartshaped.smart_fesr.data_fusion.request.RequestHandler;
import com.smartshaped.smart_fesr.data_fusion.utils.DataFusionConfigurationUtils;
import com.smartshaped.smart_fesr.ml.exception.HdfsReaderException;
import com.smartshaped.smart_fesr.preprocessing.exception.PreprocessorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sedona.spark.SedonaContext;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

/**
 * The DataFusionLayer class is responsible for starting the data fusion process. It reads all the
 * requests from the database, filters the data_fusions that match the request, and calls the {@link
 * DataFusion#execute(Request)} method for each of them.
 */
public class DataFusionLayer {

  private static final Logger logger = LogManager.getLogger(DataFusionLayer.class);

  protected DataFusionConfigurationUtils configurationUtils;
  protected SparkSession sparkSession;
  protected RequestHandler handler;
  protected List<DataFusion> dataFusions;
  protected List<DataFusion> filteredDataFusions;
  private Thread shutdownHook;

  /**
   * Constructs a DataFusionLayer instance, initializing necessary configurations.
   *
   * @throws ConfigurationException If there is an error in loading configurations
   * @throws CassandraException If there is an error in establishing Cassandra connection
   */
  public DataFusionLayer() throws ConfigurationException, CassandraException {

    configurationUtils = DataFusionConfigurationUtils.getDataFusionConf();
    logger.info("DataFusion configurations loaded correctly");

    try {
      logger.info("Loading configuration for spark session...");
      SparkConf sparkConf = configurationUtils.getSparkConf();
      SparkSession config = SedonaContext.builder().config(sparkConf).getOrCreate();
      sparkSession = SedonaContext.create(config);
      logger.info("Spark session successfully created");
    } catch (Exception e) {
      throw new ConfigurationException("Error getting or creating Sedona SparkSession", e);
    }

    handler = configurationUtils.getRequestHandler();
    logger.info("Request handler loaded correctly");
    dataFusions = configurationUtils.getDataFusions();
    logger.info("DataFusions list loaded correctly");
  }

  /**
   * Start the data fusion process.
   *
   * <p>The method reads all the requests from the database, filters the data_fusions that match the
   * request, and calls the {@link DataFusion#execute(Request)} method for each of them. If an
   * exception is thrown during the data fusion process, the request is marked as error and the
   * exception is re-thrown.
   *
   * @throws ConfigurationException If there is an error in the configuration
   * @throws HdfsReaderException If there is an error when reading from HDFS
   * @throws CassandraException If there is an error when interacting with Cassandra
   */
  public void start()
      throws ConfigurationException,
          HdfsReaderException,
          CassandraException,
          DataFusionLayerException {

    logger.info("Starting data fusion process");

    Request[] requests = handler.getRequest();
    String state = "";

    for (Request request : requests) {
      this.shutdownHook =
          new Thread(
              () -> {
                logger.info("Closing Spark Application...");
                try {
                  RequestHandler killedHandler = configurationUtils.getRequestHandler();
                  logger.info("Request value: {}", request);
                  killedHandler.updateRequestState(request, "blocked");
                } catch (CassandraException | ConfigurationException e) {
                  throw new RuntimeException(e.getMessage(), e);
                }
              });
      Runtime.getRuntime().addShutdownHook(shutdownHook);
      logger.debug("Processing request: {}", request);
      state = "inProgress";
      handler.updateRequestState(request, state);
      try {
        this.filteredDataFusions = filterDataFusions(dataFusions, request);
        for (DataFusion dataFusion : this.filteredDataFusions) {
          logger.debug("Using data_fusion: {}", dataFusion.getClass());
          dataFusion.execute(request);
        }
        state = "completed";
      } catch (DataFusionException | PreprocessorException e) {
        state = "error";
        throw new DataFusionLayerException("Error during the request: " + request, e);
      } finally {
        handler.updateRequestState(request, state);
      }
      closeDataFusionConnections();
      logger.info("Request completed");
    }
    this.closeConnections();
  }

  /**
   * Filters the list of data_fusions based on the data_fusion IDs provided in the request.
   *
   * <p>It splits the data_fusion IDs from the request into a set and loops through each
   * data_fusion, checking if its ID is present in the set. If a match is found, the data_fusion is
   * added to the resulting list.
   *
   * @param dataFusions The list of data_fusion instances to be filtered.
   * @param request The request containing data_fusion IDs used for filtering.
   * @return A list of data_fusions that match the IDs specified in the request.
   */
  private static List<DataFusion> filterDataFusions(List<DataFusion> dataFusions, Request request) {

    logger.debug("Number of data_fusions: {}", dataFusions.size());
    for (DataFusion dataFusion : dataFusions) {
      logger.debug("DataFusion: {}", dataFusion);
    }

    List<DataFusion> dataFusionList = new ArrayList<>();

    Set<String> set =
        Arrays.stream(request.getDataFusionIds().split(",")).collect(Collectors.toSet());

    logger.info("Request DataFusion IDs: {}", set);

    for (DataFusion dataFusion : dataFusions) {
      String dataFusionId = dataFusion.getDataFusionId().split("\\.")[2];
      if (set.contains(dataFusionId)) {
        dataFusionList.add(dataFusion);
      }
    }

    logger.debug("Number of filtered data_fusions: {}", dataFusionList.size());

    return dataFusionList;
  }

  /** Closes Cassandra connection and Spark Session at the end of the DataFusion Layer execution. */
  private void closeConnections() {
    this.handler.closeConnection();
    this.sparkSession.close();
    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
  }

  /**
   * Closes all opened connections through filtered DataFusion after their execution..
   *
   * @throws DataFusionLayerException If there is an error closing data_fusion pending connections.
   */
  private void closeDataFusionConnections() throws DataFusionLayerException {
    for (DataFusion dataFusion : this.filteredDataFusions) {
      try {
        dataFusion.closeConnections();
      } catch (DownloaderException | PreprocessorException e) {
        throw new DataFusionLayerException(
            "Exception raised closing pending connections for DataFusion: "
                + dataFusion.getDataFusionId(),
            e);
      }
    }
  }
}
