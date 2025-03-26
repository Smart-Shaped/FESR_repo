package com.smartshaped.smartfesr.common.utils;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;

public class ConfigurationUtilsExample extends ConfigurationUtils {

  protected ConfigurationUtilsExample() throws ConfigurationException {
    super();

    setConfRoot("test.");
  }
}
