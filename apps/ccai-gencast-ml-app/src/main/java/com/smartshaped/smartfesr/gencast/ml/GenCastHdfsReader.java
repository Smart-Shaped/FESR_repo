package com.smartshaped.smartfesr.gencast.ml;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.ml.HdfsReader;
import com.smartshaped.smartfesr.ml.exception.HdfsReaderException;

public class GenCastHdfsReader extends HdfsReader {

  public GenCastHdfsReader() throws ConfigurationException {
    super();
  }

  @Override
  public Dataset<Row> processRawData() throws HdfsReaderException {
    return this.dataframe;
  }
}
