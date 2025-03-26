package com.smartshaped.smart_fesr.data_fusion.downloader;

import java.util.List;

import com.smartshaped.smart_fesr.common.exception.ConfigurationException;
import com.smartshaped.smart_fesr.data_fusion.downloader.BinaryDownloader;
import com.smartshaped.smart_fesr.data_fusion.exception.DownloaderException;
import com.smartshaped.smart_fesr.data_fusion.request.Request;

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
