package com.smartshaped.smartfesr.ml;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.ml.exception.HdfsReaderException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class CustomReader extends HdfsReader {
  public CustomReader() throws ConfigurationException {
    /* document why this constructor is empty */
  }

  @Override
  protected Dataset<Row> processRawData() throws HdfsReaderException {
    return super.dataframe;
  }
}
