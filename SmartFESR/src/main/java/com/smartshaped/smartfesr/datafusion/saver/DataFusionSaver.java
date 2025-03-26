package com.smartshaped.smartfesr.datafusion.saver;

import com.smartshaped.smartfesr.datafusion.exception.DataFusionSaverException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/** A utility class for saving a Spark Dataset to a specified HDFS path in Parquet format. */
public class DataFusionSaver {

  private static final Logger logger = LogManager.getLogger(DataFusionSaver.class);

  /**
   * Saves a Spark Dataset to a specified HDFS path in Parquet format.
   *
   * <p>This class provides a utility method for saving a Spark Dataset to a specified HDFS path in
   * Parquet format. It is designed to be used in a static way, and it does not need to be
   * instantiated.
   */
  private DataFusionSaver() throws DataFusionSaverException {
    throw new DataFusionSaverException(
        "DataFusionSaver is an utility class and you can access to its methods in a static way");
  }

  /**
   * Saves a Spark Dataset to a specified HDFS path in Parquet format.
   *
   * <p>This method writes the given Dataset<Row> to the specified path in Parquet format using the
   * append mode. This allows new data to be appended to the existing data at the specified
   * location.
   *
   * @param df The Dataset<Row> to be saved.
   * @param parquetPath The HDFS path where the data will be stored in Parquet format.
   */
  public static void save(Dataset<Row> df, String parquetPath) {
    logger.debug("Trying to write dataset to defined path: {}", parquetPath);
    df.write().mode("append").parquet(parquetPath);
  }
}
