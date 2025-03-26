package com.smartshaped.smart_fesr.ml;

import com.smartshaped.smart_fesr.common.exception.ConfigurationException;
import com.smartshaped.smart_fesr.ml.exception.HdfsReaderException;
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
