package com.smartshaped.smartfesr.datafusion.request;

import com.smartshaped.smartfesr.common.utils.TableModel;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a request in the system.
 *
 * <p>This class models a request with an ID, state, content, and associated data_fusion IDs. It
 * extends the TableModel class and overrides the method to choose the primary key as 'id'.
 */
@Getter
@Setter
@ToString
public class Request extends TableModel {

  UUID id;
  String state;
  String content;
  String dataFusionIds;

  /**
   * {@inheritDoc}
   *
   * <p>The primary key for {@link Request} is the 'id' field.
   */
  @Override
  protected String choosePrimaryKey() {
    return "id";
  }
}
