package com.smartshaped.smartfesr.datafusion;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.request.Request;

import java.util.List;

public class DataFusionTestClass extends DataFusion {

  public DataFusionTestClass() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> extractParams(Request req) {
    return List.of();
  }
}
