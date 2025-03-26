package com.smartshaped.smart_fesr.data_fusion;

import com.smartshaped.smart_fesr.common.exception.ConfigurationException;
import com.smartshaped.smart_fesr.data_fusion.DataFusion;
import com.smartshaped.smart_fesr.data_fusion.request.Request;

import java.util.ArrayList;
import java.util.List;

public class DataFusionExample extends DataFusion {

  public DataFusionExample() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> extractParams(Request req) {
    List<String> list = new ArrayList<>();
    list.add("param1");
    list.add("param2");
    return list;
  }
}
