package com.smartshaped.smartfesr.datafusion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.exception.DownloaderException;
import com.smartshaped.smartfesr.datafusion.exception.DataFusionException;
import com.smartshaped.smartfesr.datafusion.request.Request;
import com.smartshaped.smartfesr.datafusion.request.RequestHandler;
import com.smartshaped.smartfesr.datafusion.saver.DataFusionSaver;
import com.smartshaped.smartfesr.datafusion.utils.DataFusionConfigurationUtils;

@ExtendWith(MockitoExtension.class)
class DataFusionTest {

  @Mock private Request request;
  @Mock private Dataset<Row> dataset;
  @Mock private DataFusionConfigurationUtils configurationUtils;
  @Mock private RequestHandler requestHandler;

  private List<DataFusion> dataFusionsList = new ArrayList<>();

  private List<String> paramList = new ArrayList<>();

  @Mock private DataFusion dataFusion;
  @Mock private DownloaderExample downloaderExample;
  @Mock private PreprocessorExample preprocessorExample;

  @BeforeEach
  void setup() {
    dataFusionsList.add(dataFusion);
    paramList.add("param1");
    paramList.add("param2");
  }

  @Test
  void executeSuccess() throws ConfigurationException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<DataFusionSaver> dataFusionSaver = mockStatic(DataFusionSaver.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDataFusionId(anyString())).thenReturn("data_fusion1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      DataFusionExample dataFusionExample = new DataFusionExample();
      assertDoesNotThrow(() -> dataFusionExample.execute(request));
    }
  }

  @Test
  void downloadAndTransformSuccess() throws ConfigurationException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<DataFusionSaver> dataFusionSaver = mockStatic(DataFusionSaver.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDataFusionId(anyString())).thenReturn("data_fusion1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      DataFusionExample dataFusionExample = new DataFusionExample();
      assertDoesNotThrow(() -> dataFusionExample.download(paramList, request));
    }
  }

  @Test
  void downloadAndTransformNull() throws ConfigurationException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<DataFusionSaver> dataFusionSaver = mockStatic(DataFusionSaver.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDataFusionId(anyString())).thenReturn("data_fusion1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      DataFusionExample dataFusionExample = new DataFusionExample();
      assertDoesNotThrow(() -> dataFusionExample.download(paramList, request));
    }
  }

  @Test
  void downloadAndTransformFailure() throws ConfigurationException, DownloaderException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<DataFusionSaver> dataFusionSaver = mockStatic(DataFusionSaver.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDataFusionId(anyString())).thenReturn("data_fusion1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      when(downloaderExample.download(paramList, request)).thenThrow(ConfigurationException.class);
      DataFusionExample dataFusionExample = new DataFusionExample();
      assertThrows(DataFusionException.class, () -> dataFusionExample.download(paramList, request));
    }
  }

  @Test
  void processSuccess() throws ConfigurationException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<DataFusionSaver> dataFusionSaver = mockStatic(DataFusionSaver.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDataFusionId(anyString())).thenReturn("data_fusion1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      when(configurationUtils.getPreprocessor(anyString())).thenReturn(preprocessorExample);

      DataFusionExample dataFusionExample = new DataFusionExample();
      assertDoesNotThrow(() -> dataFusionExample.process(dataset));
    }
  }

  @Test
  void processNull() throws ConfigurationException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<DataFusionSaver> dataFusionSaver = mockStatic(DataFusionSaver.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDataFusionId(anyString())).thenReturn("data_fusion1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);

      DataFusionExample dataFusionExample = new DataFusionExample();
      assertDoesNotThrow(() -> dataFusionExample.process(dataset));
    }
  }
}
