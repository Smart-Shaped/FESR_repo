package com.smartshaped.smartfesr.ml;

import com.smartshaped.smartfesr.ml.exception.PipelineException;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.ml.Model;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Abstract class representing a machine learning pipeline.
 *
 * <p>This class provides a standard interface for all the machine learning pipelines.
 *
 * <p>All the machine learning pipelines must extend this class and implement the methods.
 */
@Getter
@Setter
public abstract class Pipeline {

  private static final Logger logger = LogManager.getLogger(Pipeline.class);

  protected List<Dataset<Row>> datasets;
  protected Model<?> model;
  protected Dataset<Row> predictions;

  public abstract void start() throws PipelineException;

  public abstract void evaluatePredictions(Dataset<Row> predictions) throws PipelineException;

  public abstract void evaluateModel(Model<?> model) throws PipelineException;

  public abstract Model<?> readModelFromHDFS(String hdfsPath) throws PipelineException;

  /**
   * Method to check if a given HDFS path already exists.
   *
   * @param hdfsPath String representing the path to check.
   * @return boolean indicating whether the path already exists.
   * @throws PipelineException if any error occurs while checking the path.
   */
  public boolean hdfsPathAlreadyExist(String hdfsPath) throws PipelineException {

    logger.debug("Checking HDFS path: {}", hdfsPath);

    Configuration configuration = new Configuration();
    Path path = new Path(hdfsPath);

    try {
      FileSystem fileSystem = FileSystem.get(configuration);
      return fileSystem.exists(path);
    } catch (IOException e) {
      throw new PipelineException("Error checking HDFS path", e);
    }
  }
}
