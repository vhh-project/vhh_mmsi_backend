package eu.vhhproject.mmsi.shotservice.error;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Error Details: malformedJson")
public class ErrorDetailsMalformedJson {

  @ApiModelProperty(position = 1)
  private String message;

  @ApiModelProperty(position = 2)
  private int line;

  @ApiModelProperty(position = 3)
  private int column;

  public ErrorDetailsMalformedJson(String message) {
    this.message = message;
  }

}
