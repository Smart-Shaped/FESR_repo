package com.smartshaped.smartfesr.datafusion.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;

import java.util.List;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.smartfesr.common.exception.CassandraException;
import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.common.utils.CassandraUtils;

@ExtendWith(MockitoExtension.class)
class DataFusionConfigurationUtilsTest {

  @Mock private CassandraUtils cassandraUtils;
  @Mock private YAMLConfiguration yamlConfig;

  @InjectMocks private DataFusionConfigurationUtils configurationUtils;

  @Test
  void getDataFusionConf() {
    assertDoesNotThrow(DataFusionConfigurationUtils::getDataFusionConf);
  }

  @Test
  void getDataFusionIdSuccess() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(
        () ->
            dataFusionConfigurationUtils.getDataFusionId(
                "com.smartshaped.smartfesr.datafusion.DataFusionExample"));
  }

  @Test
  void getDataFusionIdFailure() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertThrows(
        ConfigurationException.class,
        () -> dataFusionConfigurationUtils.getDataFusionId("testFailure"));
  }

  @Test
  void getDataFusionsSuccess() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(dataFusionConfigurationUtils::getDataFusions);
  }

  @Test
  void getDataFusionsFailure() {
    List<String> keys = List.of("test.data_fusions.test.class");
    doReturn(keys.iterator()).when(yamlConfig).getKeys(any());
    doReturn(null).when(yamlConfig).getString(any());

    assertThrows(ConfigurationException.class, configurationUtils::getDataFusions);
  }

  @Test
  void getDataFusionsInstatiationFailure() {
    List<String> keys = List.of("test.data_fusions.test.class");
    doReturn(keys.iterator()).when(yamlConfig).getKeys(any());
    doReturn("test").when(yamlConfig).getString(any());

    assertThrows(ConfigurationException.class, configurationUtils::getDataFusions);
  }

  @Test
  void getRequestHandlerSuccessOne() throws ConfigurationException, CassandraException {
    try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
      mockedStaticCassandra
          .when(() -> CassandraUtils.getCassandraUtils(any()))
          .thenReturn(cassandraUtils);

      doNothing().when(cassandraUtils).validateTableModel(any());

      DataFusionConfigurationUtils dataFusionConfigurationUtils =
          DataFusionConfigurationUtils.getDataFusionConf();
      assertDoesNotThrow(dataFusionConfigurationUtils::getRequestHandler);
    }
  }

  @Test
  void getRequestHandlerNull() throws ConfigurationException {

    try (MockedStatic<DataFusionConfigurationUtils> mockedStatic =
        mockStatic(DataFusionConfigurationUtils.class)) {
      mockedStatic
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);

        DataFusionConfigurationUtils dataFusionConfigurationUtils =
            DataFusionConfigurationUtils.getDataFusionConf();
        doReturn(null).when(yamlConfig).getString("data_fusion.RequestHandler");
        assertThrows(ConfigurationException.class, dataFusionConfigurationUtils::getRequestHandler);
      }
    }
  }

  @Test
  void getRequestHandlerFailure() throws ConfigurationException {

    try (MockedStatic<DataFusionConfigurationUtils> mockedStatic =
        mockStatic(DataFusionConfigurationUtils.class)) {
      mockedStatic
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);

        DataFusionConfigurationUtils dataFusionConfigurationUtils =
            DataFusionConfigurationUtils.getDataFusionConf();
        doReturn("wrongRequestHandler").when(yamlConfig).getString("data_fusion.RequestHandler");
        assertThrows(ConfigurationException.class, dataFusionConfigurationUtils::getRequestHandler);
      }
    }
  }

  @Test
  void getPreprocessorParams() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(
        () -> dataFusionConfigurationUtils.getPreprocessorParams("DataFusionExample"));
  }

  @Test
  void getPreprocessorParamsNotFound() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(() -> dataFusionConfigurationUtils.getPreprocessorParams(""));
  }

  @Test
  void getParamsNotFound() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(() -> dataFusionConfigurationUtils.getQueryParam(""));
  }

  @Test
  void getPreprocessorSuccess() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(
        () ->
            dataFusionConfigurationUtils.getPreprocessor("data_fusion.data_fusions.data_fusion1"));
  }

  @Test
  void getPreprocessorNotFound() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(
        () ->
            dataFusionConfigurationUtils.getPreprocessor("data_fusion.data_fusions.data_fusion2"));
  }

  @Test
  void getPreprocessorFailure() throws ConfigurationException {
    try (MockedStatic<DataFusionConfigurationUtils> mockedStatic =
        mockStatic(DataFusionConfigurationUtils.class)) {
      mockedStatic
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);

      DataFusionConfigurationUtils dataFusionConfigurationUtils =
          DataFusionConfigurationUtils.getDataFusionConf();
      doReturn("wrongPreprocessorClass")
          .when(yamlConfig)
          .getString("dataFusionId.preprocessor", "");
      assertThrows(
          ConfigurationException.class,
          () -> dataFusionConfigurationUtils.getPreprocessor("dataFusionId"));
    }
  }

  @Test
  void getUrlParamsSuccess() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(() -> dataFusionConfigurationUtils.getUrlParams("DownloaderExample"));
  }

  @Test
  void getQueryParamSuccess() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(() -> dataFusionConfigurationUtils.getQueryParam("DownloaderExample"));
  }

  @Test
  void getDownloader() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertDoesNotThrow(
        () -> dataFusionConfigurationUtils.getDownloader("data_fusion.data_fusions.data_fusion1"));
  }

  @Test
  void getDownloaderEmpty() throws ConfigurationException {
    DataFusionConfigurationUtils dataFusionConfigurationUtils =
        DataFusionConfigurationUtils.getDataFusionConf();
    assertThrows(
        ConfigurationException.class,
        (() ->
            dataFusionConfigurationUtils.getDownloader("data_fusion.data_fusions.data_fusion2")));
  }

  @Test
  void getDownloaderFailure() throws ConfigurationException {
    try (MockedStatic<DataFusionConfigurationUtils> mockedStatic =
        mockStatic(DataFusionConfigurationUtils.class)) {
      mockedStatic
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);

      DataFusionConfigurationUtils dataFusionConfigurationUtils =
          DataFusionConfigurationUtils.getDataFusionConf();
      doReturn("wrongPreprocessorClass")
          .when(yamlConfig)
          .getString("dataFusionId.downloader.class");
      assertThrows(
          ConfigurationException.class,
          () -> dataFusionConfigurationUtils.getDownloader("dataFusionId"));
    }
  }
}
