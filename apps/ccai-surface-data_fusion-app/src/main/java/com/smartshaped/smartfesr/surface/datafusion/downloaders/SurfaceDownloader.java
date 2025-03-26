package com.smartshaped.smartfesr.surface.datafusion.downloaders;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.downloader.FTPDownloader;
import com.smartshaped.smartfesr.datafusion.request.Request;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.net.ftp.FTPFile;

public class SurfaceDownloader extends FTPDownloader {
  public SurfaceDownloader() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> createUriList(List<String> list, Request request) {
    List<String> result = new ArrayList<>();
    try {
      for (String folderPath : list) {
        Collection<String> files = listFiles(folderPath);
        for (String fileName : files) {
          String fullPath = folderPath + File.separator + fileName;
          result.add(fullPath);
        }
      }
    } catch (IOException e) {
      logger.error("Error creating URI list", e);
    }

    logger.info("FTP Paths: {}", result);
    return result;
  }

  Collection<String> listFiles(String path) throws IOException {
    FTPFile[] files = ftpClient.listFiles(path);
    return Arrays.stream(files)
        .filter(FTPFile::isFile)
        .map(FTPFile::getName)
        .collect(Collectors.toList());
  }
}
