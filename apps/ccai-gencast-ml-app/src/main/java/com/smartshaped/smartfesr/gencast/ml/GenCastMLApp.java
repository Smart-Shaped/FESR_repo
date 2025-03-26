package com.smartshaped.smartfesr.gencast.ml;

import com.smartshaped.smartfesr.common.exception.CassandraException;
import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.ml.blackbox.exception.BlackboxException;
import com.smartshaped.smartfesr.ml.exception.HdfsReaderException;
import com.smartshaped.smartfesr.ml.exception.MLLayerException;
import com.smartshaped.smartfesr.ml.exception.ModelSaverException;
import com.smartshaped.smartfesr.ml.exception.PipelineException;

public class GenCastMLApp {

  public static void main(String[] args)
      throws ConfigurationException,
          MLLayerException,
          PipelineException,
          HdfsReaderException,
          BlackboxException,
          ModelSaverException,
          CassandraException {
    GenCastMLLayer layer = new GenCastMLLayer();
    layer.start();
  }
}
