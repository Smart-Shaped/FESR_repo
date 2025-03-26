package com.smartshaped.smartfesr.datafusion.downloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.request.Request;

class DownloaderTest {

  @Mock private List<String> paramList;
  @Mock private Request request;

  @Test
  void testDownloaderSuccess() throws ConfigurationException {
    Downloader downloader = new DownloaderClassTest();
    assertDoesNotThrow(() -> downloader.download(paramList, request));
  }
}
