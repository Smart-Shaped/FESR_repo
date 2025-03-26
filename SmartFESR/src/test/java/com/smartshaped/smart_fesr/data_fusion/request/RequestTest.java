package com.smartshaped.smart_fesr.data_fusion.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.smart_fesr.data_fusion.request.Request;

@ExtendWith(MockitoExtension.class)
class RequestTest {

  private Request request;

  @BeforeEach
  void setUp() {
    request = new Request();
  }

  @Test
  void testGettersAndSetters() {
    UUID id = UUID.randomUUID();
    String state = "ACTIVE";
    String content = "Test Content";
    String dataFusionIds = "123,456";

    request.setId(id);
    request.setState(state);
    request.setContent(content);
    request.setDataFusionIds(dataFusionIds);

    assertEquals(id, request.getId());
    assertEquals(state, request.getState());
    assertEquals(content, request.getContent());
    assertEquals(dataFusionIds, request.getDataFusionIds());
  }

  @Test
  void testToString() {
    UUID id = UUID.randomUUID();
    request.setId(id);
    request.setState("ACTIVE");
    request.setContent("Test Content");
    request.setDataFusionIds("123,456");

    String expectedString =
        "Request(id=" + id + ", state=ACTIVE, content=Test Content, dataFusionIds=123,456)";
    assertEquals(expectedString, request.toString());
  }

  @Test
  void testChoosePrimaryKey() {
    assertEquals("id", request.choosePrimaryKey());
  }

  @Test
  void testDefaultValues() {
    assertNull(request.getId());
    assertNull(request.getState());
    assertNull(request.getContent());
    assertNull(request.getDataFusionIds());
  }
}
