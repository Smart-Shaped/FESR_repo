package com.smartshaped.smartfesr.datafusion.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataFusionLayerExceptionTest {

  @Test
  void testDataFusionLayerExceptionThrown() {
    assertThrows(
        DataFusionLayerException.class,
        () -> {
          throw new DataFusionLayerException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the DataFusion Layer";
    DataFusionLayerException exception = new DataFusionLayerException(errorMessage);

    String expectedMessage = "Exception in the DataFusion Layer. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    DataFusionLayerException exception =
        assertThrows(
            DataFusionLayerException.class,
            () -> {
              throw new DataFusionLayerException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testDataFusionLayerExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    DataFusionLayerException exception = new DataFusionLayerException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the DataFusion Layer")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testDataFusionLayerExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    DataFusionLayerException exception = new DataFusionLayerException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
