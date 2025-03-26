package com.smartshaped.smart_fesr.data_fusion;

import com.smartshaped.smart_fesr.common.exception.ConfigurationException;
import com.smartshaped.smart_fesr.data_fusion.DataFusion;
import com.smartshaped.smart_fesr.data_fusion.request.Request;

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
