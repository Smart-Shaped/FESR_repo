package com.smartshaped.smartfesr.surface.datafusion;

import com.smartshaped.smartfesr.common.exception.CassandraException;
import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.exception.DataFusionLayerException;
import com.smartshaped.smartfesr.ml.exception.HdfsReaderException;

public class DataFusionMain {

  public static void main(String[] args)
      throws HdfsReaderException,
          ConfigurationException,
          CassandraException,
          DataFusionLayerException {

    CustomDataFusionLayer dataFusionLayer = new CustomDataFusionLayer();
    dataFusionLayer.start();
  }
}
