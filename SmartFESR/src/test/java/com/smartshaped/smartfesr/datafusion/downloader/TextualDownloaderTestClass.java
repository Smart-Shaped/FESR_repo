package com.smartshaped.smartfesr.datafusion.downloader;

import java.util.List;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.exception.DownloaderException;
import com.smartshaped.smartfesr.datafusion.request.Request;

public class TextualDownloaderTestClass extends TextualDownloader {
  protected TextualDownloaderTestClass() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException {
    return List.of();
  }

  @Override
  public void closeConnections() throws DownloaderException {}
}
