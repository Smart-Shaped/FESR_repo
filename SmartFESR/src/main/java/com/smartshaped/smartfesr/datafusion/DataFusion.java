package com.smartshaped.smartfesr.datafusion;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.downloader.Downloader;
import com.smartshaped.smartfesr.datafusion.exception.DataFusionException;
import com.smartshaped.smartfesr.datafusion.exception.DownloaderException;
import com.smartshaped.smartfesr.datafusion.request.Request;
import com.smartshaped.smartfesr.datafusion.saver.DataFusionSaver;
import com.smartshaped.smartfesr.datafusion.utils.DataFusionConfigurationUtils;
import com.smartshaped.smartfesr.preprocessing.Preprocessor;
import com.smartshaped.smartfesr.preprocessing.exception.PreprocessorException;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * The DataFusion class is responsible for downloading and transforming data, and then saving it to
 * a file.
 */
public abstract class DataFusion {

  private static final Logger logger = LogManager.getLogger(DataFusion.class);
  protected String outputPath;
  protected String inputPath;
  protected SparkSession session;
  @Getter private final String dataFusionId;
  private final Preprocessor preprocessor;
  private final DataFusionConfigurationUtils configurationUtils;
  private final Downloader downloader;

  /**
   * Constructs a DataFusion instance with configurations loaded from the
   * DataFusionConfigurationUtils.
   *
   * @throws ConfigurationException If there is an error loading the configurations.
   */
  protected DataFusion() throws ConfigurationException {
    configurationUtils = DataFusionConfigurationUtils.getDataFusionConf();
    logger.info("DataFusion configurations loaded correctly");
    String className = this.getClass().getName();
    dataFusionId = configurationUtils.getDataFusionId(className);
    logger.debug("dataFusionId \"{}\" loaded correctly", dataFusionId);
    outputPath = configurationUtils.getOutputPath(dataFusionId);
    logger.debug("outputPath \\\"{}\\\" loaded correctly", outputPath);
    inputPath = configurationUtils.getInputPath(dataFusionId);
    logger.debug("inputPath \\\"{}\\\" loaded correctly", inputPath);
    logger.info("Loading preprocessor...");
    preprocessor = configurationUtils.getPreprocessor(dataFusionId);
    logger.info("Loading downloader...");
    downloader = configurationUtils.getDownloader(dataFusionId);
  }

  /**
   * Extracts the parameters needed for the data fusion process from the given request.
   *
   * @param req The request from which the parameters should be extracted.
   * @return The extracted parameters.
   */
  protected abstract List<String> extractParams(Request req);

  /**
   * Starts the data fusion process. This method is responsible for calling the correct methods in
   * the correct order to complete the data fusion process.
   *
   * @param req The request that contains the parameters needed for the data fusion process.
   * @throws DataFusionException If there is an error during the data fusion process.
   * @throws PreprocessorException If there is an error during the preprocessing step.
   */
  public void execute(Request req) throws DataFusionException, PreprocessorException {
    List<String> paramList = extractParams(req);
    logger.debug("Extracted params: {}", paramList);

    logger.info("Downloading and transforming data...");
    Dataset<Row> data = download(paramList, req);
    logger.info("Downloaded and transformed data");

    logger.info("Starting preprocessing...");
    Dataset<Row> df = process(data);
    logger.info("Data preprocessing completed");

    DataFusionSaver.save(df, outputPath);
    logger.info("Saved data to: {}", outputPath);
  }

  /**
   * Downloads the data specified by the request and transforms it according to the data_fusion's
   * configuration.
   *
   * @param paramList The list of parameters needed for the data fusion process.
   * @param req The request that contains the parameters needed for the data fusion process.
   * @return The downloaded and transformed data.
   * @throws DataFusionException If there is an error during the downloading or transforming
   *     process.
   */
  public Dataset<Row> download(List<String> paramList, Request req) throws DataFusionException {
    Dataset<Row> df;

    try {
      df = downloader.download(paramList, req);
    } catch (DownloaderException | ConfigurationException | IOException e) {
      throw new DataFusionException("Error downloading or transforming data.", e);
    }

    return df;
  }

  /**
   * Processes the data using the preprocessor associated with this data_fusion.
   *
   * @param data The data to be processed.
   * @return The processed data.
   * @throws PreprocessorException If there is an error during the preprocessing step.
   */
  public Dataset<Row> process(Dataset<Row> data) throws PreprocessorException {
    if (preprocessor == null) {
      logger.warn("Preprocessor not defined for DataFusion {}", dataFusionId);
      return data;
    } else {
      return preprocessor.preprocess(data);
    }
  }

  /**
   * Closes all opened connections from the associated downloader or preprocessor.
   *
   * @throws DownloaderException If there is an error closing downloader connections.
   * @throws PreprocessorException If there is an error closing preprocessor connections.
   */
  public void closeConnections() throws DownloaderException, PreprocessorException {
    this.downloader.closeConnections();
    if (this.preprocessor != null) {
      this.preprocessor.closeConnections();
    }
  }
}
