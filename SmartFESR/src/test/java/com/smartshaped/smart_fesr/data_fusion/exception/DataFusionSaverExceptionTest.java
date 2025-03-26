package com.smartshaped.smart_fesr.data_fusion.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.smart_fesr.data_fusion.exception.DataFusionSaverException;

@ExtendWith(MockitoExtension.class)
class DataFusionSaverExceptionTest {

  @Test
  void testDataFusionSaverExceptionThrown() {
    assertThrows(
        DataFusionSaverException.class,
        () -> {
          throw new DataFusionSaverException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the DataFusionSaver";
    DataFusionSaverException exception = new DataFusionSaverException(errorMessage);

    String expectedMessage = "Exception in the DataFusionSaver. Caused by : \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    DataFusionSaverException exception =
        assertThrows(
            DataFusionSaverException.class,
            () -> {
              throw new DataFusionSaverException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testDataFusionExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    DataFusionSaverException exception = new DataFusionSaverException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the DataFusion")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testDataFusionExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    DataFusionSaverException exception = new DataFusionSaverException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
