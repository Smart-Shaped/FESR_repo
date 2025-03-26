package com.smartshaped.smartfesr.surface.datafusion.datafusions;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.DataFusion;
import com.smartshaped.smartfesr.datafusion.request.Request;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SurfaceDataFusion extends DataFusion {
  private static final Logger logger = LogManager.getLogger(SurfaceDataFusion.class);

  public SurfaceDataFusion() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> extractParams(Request req) {
    String[] paths = req.getContent().split(",");
    return Arrays.asList(paths);
  }
}
