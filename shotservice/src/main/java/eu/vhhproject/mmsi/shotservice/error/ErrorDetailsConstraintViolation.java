package eu.vhhproject.mmsi.shotservice.error;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Error Details: constraintViolation")
public class ErrorDetailsConstraintViolation {

  @ApiModelProperty(position = 1)
  private String message;

  @ApiModelProperty(position = 2)
  private String property;

  @ApiModelProperty(position = 3)
  private Object invalidValue;

}
