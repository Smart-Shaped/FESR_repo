package com.smartshaped.smartfesr.datafusion.downloader;

import java.util.List;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.exception.DownloaderException;
import com.smartshaped.smartfesr.datafusion.request.Request;

public class BinaryDownloaderTestClass extends BinaryDownloader {

  protected BinaryDownloaderTestClass() throws ConfigurationException {
    super();
  }

  @Override
  protected String createStructuredFileName(String url) {
    return "";
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException {
    return List.of();
  }

  @Override
  public void closeConnections() throws DownloaderException {}
}
