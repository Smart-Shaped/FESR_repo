package com.smartshaped.smartfesr.gencast.downloader;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.downloader.BinaryDownloader;
import com.smartshaped.smartfesr.datafusion.exception.DownloaderException;
import com.smartshaped.smartfesr.datafusion.request.Request;
import com.smartshaped.smartfesr.datafusion.utils.DataFusionConfigurationUtils;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GencastNcDownloader extends BinaryDownloader {
  private static final Logger logger = LogManager.getLogger(GencastNcDownloader.class);
  private final HttpClient httpClient;
  private final DataFusionConfigurationUtils dataFusionConfigurationUtils;

  public GencastNcDownloader() throws ConfigurationException {
    super();
    this.dataFusionConfigurationUtils = DataFusionConfigurationUtils.getDataFusionConf();
    this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    logger.info("GencastNcDownloader instance created.");
  }

  @Override
  protected List<String> createUriList(List<String> list, Request request) {
    String root = urlParams.get("basePath");
    List<String> uriList = new ArrayList<>();
    for (String fileName : list) {
      String url = root + fileName;
      uriList.add(url);
    }
    return uriList;
  }

  @Override
  public void closeConnections() throws DownloaderException {

  }

  @Override
  protected String createStructuredFileName(String url) {
    int lastSlashIndex = url.lastIndexOf("/");
    int secondLastSlashIndex = url.lastIndexOf("/", lastSlashIndex - 1);
    
    if (secondLastSlashIndex == -1) {
    	return url;
    } else {
    	return url.substring(secondLastSlashIndex + 1);
    }
  }
}
