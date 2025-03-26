package com.smartshaped.smartfesr.datafusion.request;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestValidatorTest {

  private Request request = new Request();

  @BeforeEach
  void setUp() {
    request.setId(UUID.randomUUID());
    request.setContent("test");
    request.setState("false");
    request.setDataFusionIds("data_fusion1");
  }

  @Test
  void isRequestValidSuccess() {
    RequestValidator validator = new RequestValidator();
    assertDoesNotThrow(() -> validator.isRequestValid(request));
  }

  @Test
  void isRequestValidNull() {
    request = null;
    RequestValidator validator = new RequestValidator();
    assertDoesNotThrow(() -> validator.isRequestValid(request));
  }

  @Test
  void isRequestValidNullId() {
    request.setId(null);
    RequestValidator validator = new RequestValidator();
    assertDoesNotThrow(() -> validator.isRequestValid(request));
  }

  @Test
  void isRequestValidNullState() {
    request.setState(null);
    RequestValidator validator = new RequestValidator();
    assertDoesNotThrow(() -> validator.isRequestValid(request));
  }

  @Test
  void isRequestValidNullContent() {
    request.setContent(null);
    RequestValidator validator = new RequestValidator();
    assertDoesNotThrow(() -> validator.isRequestValid(request));
  }

  @Test
  void isRequestValidNullIds() {
    request.setDataFusionIds(null);
    RequestValidator validator = new RequestValidator();
    assertDoesNotThrow(() -> validator.isRequestValid(request));
  }
}
