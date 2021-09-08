package eu.vhhproject.mmsi.shotservice.api.tos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Video")
public class VideoTO {

  @ApiModelProperty(position = 1)
  private Long id;

  // ignore this field as it is not clear which value CA is using for this field
  @JsonIgnore
  @ApiModelProperty(position = 2)
  private String displayLabel;

  @ApiModelProperty(position = 3)
  private String originalFileName;

  @ApiModelProperty(position = 4)
  private String url;

  @ApiModelProperty(position = 5)
  private boolean processed;
}
