package com.smartshaped.smartfesr.datafusion;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.preprocessing.Preprocessor;
import com.smartshaped.smartfesr.preprocessing.exception.PreprocessorException;

public class PreprocessorExample extends Preprocessor {

  public PreprocessorExample() throws ConfigurationException {
    super();
  }

  @Override
  public Dataset<Row> preprocess(Dataset<Row> ds) throws PreprocessorException {
    SparkSession sparkSession = SparkSession.getActiveSession().get();
    Dataset<Row> df = sparkSession.createDataFrame(List.of(), Row.class);
    return df;
  }

  @Override
  public void closeConnections() throws PreprocessorException {}
}
