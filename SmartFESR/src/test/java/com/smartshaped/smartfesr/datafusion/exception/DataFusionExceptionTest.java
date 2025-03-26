package com.smartshaped.smartfesr.datafusion.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataFusionExceptionTest {

  @Test
  void testDataFusionExceptionThrown() {
    assertThrows(
        DataFusionException.class,
        () -> {
          throw new DataFusionException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the DataFusion";
    DataFusionException exception = new DataFusionException(errorMessage);

    String expectedMessage = "Exception in the DataFusion. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    DataFusionException exception =
        assertThrows(
            DataFusionException.class,
            () -> {
              throw new DataFusionException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testDataFusionExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    DataFusionException exception = new DataFusionException(cause);

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
    DataFusionException exception = new DataFusionException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
