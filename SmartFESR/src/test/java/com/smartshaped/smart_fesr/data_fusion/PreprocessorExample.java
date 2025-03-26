package com.smartshaped.smart_fesr.data_fusion;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.smartshaped.smart_fesr.common.exception.ConfigurationException;
import com.smartshaped.smart_fesr.preprocessing.Preprocessor;
import com.smartshaped.smart_fesr.preprocessing.exception.PreprocessorException;

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
