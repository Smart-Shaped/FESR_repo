package com.smartshaped.smart_fesr.common.utils;

import com.smartshaped.smart_fesr.common.exception.ConfigurationException;

public class ConfigurationUtilsExample extends ConfigurationUtils {

  protected ConfigurationUtilsExample() throws ConfigurationException {
    super();

    setConfRoot("test.");
  }
}
