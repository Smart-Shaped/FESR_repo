package com.smartshaped.smart_fesr.data_fusion.saver;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.smart_fesr.data_fusion.saver.DataFusionSaver;

@ExtendWith(MockitoExtension.class)
class DataFusionSaverTest {

  @Mock private Dataset<Row> dataSet;

  @Mock private DataFrameWriter<Row> dataFrame;

  String parquetPath;

  @Test
  void save() {
    when(dataSet.write()).thenReturn(dataFrame);
    when(dataFrame.mode("append")).thenReturn(dataFrame);

    assertDoesNotThrow(() -> DataFusionSaver.save(dataSet, parquetPath));
  }
}
