package eu.vhhproject.mmsi.shotservice.error;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class InternalServiceException extends Exception {

  public static final String ERROR_CODE = "internalServiceNotWorking";

  private static final long serialVersionUID = 1L;

  private String serviceName;

  public InternalServiceException(String serviceName, Exception exception) {
    super(exception);
    this.serviceName = serviceName;
  }

  public Map<String, Object> getErrorDetails()
  {
    Map<String, Object> errorDetails = new LinkedHashMap<>();

    errorDetails.put("serviceName", this.serviceName);

    return errorDetails;
  }
}
