package com.smartshaped.smartfesr.datafusion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.sedona.spark.SedonaContext;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.smartfesr.common.exception.CassandraException;
import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.common.utils.CassandraUtils;
import com.smartshaped.smartfesr.datafusion.request.Request;
import com.smartshaped.smartfesr.datafusion.request.RequestHandler;
import com.smartshaped.smartfesr.datafusion.utils.DataFusionConfigurationUtils;

@ExtendWith(MockitoExtension.class)
class DataFusionLayerTest {

  @Mock private DataFusionConfigurationUtils configurationUtils;
  @Mock private RequestHandler requestHandler;
  @Mock private SparkConf sparkConf;
  @Mock private SparkSession sparkSession;
  @Mock private SparkSession.Builder builder;

  private Request[] requestList = new Request[1];
  private List<DataFusion> dataFusionlist = new ArrayList<>();

  @Mock private Request request;
  @Mock private DataFusion dataFusion;

  @BeforeEach
  void setUp() {
    requestList[0] = request;
    dataFusionlist.add(dataFusion);
  }

  @Test
  void DataFusionlayerSuccess() throws ConfigurationException, CassandraException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<CassandraUtils> dataFusionSaver = mockStatic(CassandraUtils.class);
        MockedStatic<SedonaContext> sedonaContext = mockStatic(SedonaContext.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      sedonaContext.when(SedonaContext::builder).thenReturn(builder);
      when(configurationUtils.getRequestHandler()).thenReturn(requestHandler);
      when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
      when(builder.config(sparkConf)).thenReturn(builder);
      when(builder.getOrCreate()).thenReturn(sparkSession);

      assertDoesNotThrow(DataFusionLayer::new);
    }
  }

  @Test
  void DataFusionLayerFailure() throws ConfigurationException, CassandraException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<CassandraUtils> dataFusionSaver = mockStatic(CassandraUtils.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);

      assertThrows(ConfigurationException.class, DataFusionLayer::new);
    }
  }

  @Test
  void startSuccess() throws ConfigurationException, CassandraException {
    try (MockedStatic<DataFusionConfigurationUtils> confUtils =
            mockStatic(DataFusionConfigurationUtils.class);
        MockedStatic<CassandraUtils> dataFusionSaver = mockStatic(CassandraUtils.class);
        MockedStatic<SedonaContext> sedonaContext = mockStatic(SedonaContext.class)) {
      confUtils
          .when(DataFusionConfigurationUtils::getDataFusionConf)
          .thenReturn(configurationUtils);
      sedonaContext.when(SedonaContext::builder).thenReturn(builder);
      when(configurationUtils.getRequestHandler()).thenReturn(requestHandler);
      when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
      when(configurationUtils.getDataFusions()).thenReturn(dataFusionlist);
      when(builder.config(sparkConf)).thenReturn(builder);
      when(builder.getOrCreate()).thenReturn(sparkSession);
      sedonaContext.when(() -> SedonaContext.create(sparkSession)).thenReturn(sparkSession);

      when(requestHandler.getRequest()).thenReturn(requestList);
      when(request.getDataFusionIds()).thenReturn("data_fusion1");
      when(dataFusion.getDataFusionId()).thenReturn("data_fusion.data_fusions.dataFusion1");

      DataFusionLayer dataFusionLayer = new DataFusionLayer();
      assertDoesNotThrow(dataFusionLayer::start);
    }
  }
}
