package com.smartshaped.smartfesr.datafusion;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.downloader.Downloader;
import com.smartshaped.smartfesr.datafusion.exception.DownloaderException;
import com.smartshaped.smartfesr.datafusion.request.Request;

public class DownloaderExample extends Downloader {

  public DownloaderExample() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request) {
    return List.of();
  }

  @Override
  public Dataset<Row> download(List<String> reqParams, Request req)
      throws DownloaderException, ConfigurationException {
    return null;
  }

  @Override
  public void closeConnections() throws DownloaderException {}
}
