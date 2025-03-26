package com.smartshaped.smart_fesr.ml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.smartshaped.smart_fesr.ml.exception.PipelineException;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.ml.Model;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PipelineTest {

  String path;
  @Mock Model<?> model;
  @Mock Dataset<Row> predictions;
  PipelineExample pipeline;

  @BeforeEach
  public void setup() {
    pipeline = new PipelineExample();
    path = "path";
  }

  @Test
  void testStart() {
    assertDoesNotThrow(pipeline::start);
  }

  @Test
  void testEvaluatePredictions() {
    assertDoesNotThrow(() -> pipeline.evaluatePredictions(predictions));
  }

  @Test
  void testEvaluateModel() {
    assertDoesNotThrow(() -> pipeline.evaluateModel(model));
  }

  @Test
  void testReadModelFromHDFS() {
    assertDoesNotThrow(() -> pipeline.readModelFromHDFS(path));
  }

  @Test
  void testHdfsPathAlreadyExistSuccess() {
    assertDoesNotThrow(() -> pipeline.hdfsPathAlreadyExist(path));
  }

  @Test
  void testHdfsPathAlreadyExistFailure() throws IOException {

    try (MockedStatic<FileSystem> mockedStatic = mockStatic(FileSystem.class)) {

      mockedStatic
          .when((MockedStatic.Verification) FileSystem.get(any()))
          .thenThrow(IOException.class);
      assertThrows(PipelineException.class, () -> pipeline.hdfsPathAlreadyExist(path));
    }
  }
}
