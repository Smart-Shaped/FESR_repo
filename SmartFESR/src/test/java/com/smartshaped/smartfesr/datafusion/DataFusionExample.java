package com.smartshaped.smartfesr.datafusion;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.request.Request;

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
