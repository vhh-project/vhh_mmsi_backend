package eu.vhhproject.mmsi.shotservice.api.tos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import eu.vhhproject.mmsi.shotservice.model.enums.CameraMovement;
import eu.vhhproject.mmsi.shotservice.model.enums.ShotType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Shot")
public class ShotTO {

  @Min(1)
  @NotNull
  @ApiModelProperty(position = 1, required = true, example = "1")
  private Integer inPoint;

  @Min(1)
  @NotNull
  @ApiModelProperty(position = 2, required = true, example = "100")
  private Integer outPoint;

  @ApiModelProperty(position = 3)
  private ShotType shotType;

  @ApiModelProperty(position = 4)
  private CameraMovement cameraMovement;
}
