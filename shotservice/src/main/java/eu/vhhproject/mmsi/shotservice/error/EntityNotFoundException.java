package eu.vhhproject.mmsi.shotservice.error;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends Exception {

  public static final String ERROR_CODE = "entityNotFound";

  private static final long serialVersionUID = 1L;

  private String entityType;

  private Long id;

  public EntityNotFoundException(String entityType, Long id) {
    super();
    this.entityType = entityType;
    this.id = id;
  }

  public Map<String, Object> getErrorDetails()
  {
    Map<String, Object> errorDetails = new LinkedHashMap<>();

    errorDetails.put("entityType", this.entityType);
    errorDetails.put("id", this.id);

    return errorDetails;
  }

}
