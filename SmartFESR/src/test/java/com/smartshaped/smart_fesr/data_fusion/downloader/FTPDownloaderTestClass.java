package com.smartshaped.smart_fesr.data_fusion.downloader;

import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import com.smartshaped.smart_fesr.common.exception.ConfigurationException;
import com.smartshaped.smart_fesr.data_fusion.downloader.FTPDownloader;
import com.smartshaped.smart_fesr.data_fusion.exception.DownloaderException;
import com.smartshaped.smart_fesr.data_fusion.request.Request;

public class FTPDownloaderTestClass extends FTPDownloader {

  protected FTPDownloaderTestClass() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException {
    return List.of("test");
  }

  public void setFtpClient(FTPClient ftpClient) {
    this.ftpClient = ftpClient;
  }
}
