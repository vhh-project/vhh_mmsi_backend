package eu.vhhproject.mmsi.shotservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import eu.vhhproject.mmsi.shotservice.model.enums.CameraMovement;
import eu.vhhproject.mmsi.shotservice.model.enums.ShotType;
import eu.vhhproject.mmsi.shotservice.support.SizeConstraints;
import lombok.Data;

@Data
@Entity
public class Shot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private Long videoId;

  @Min(1)
  @NotNull
  private Integer inPoint;

  @Min(1)
  @NotNull
  private Integer outPoint;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "varchar(" + SizeConstraints.MAX_LENGTH_ENUM + ") default null")
  private ShotType shotType;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "varchar(" + SizeConstraints.MAX_LENGTH_ENUM + ") default null")
  private CameraMovement cameraMovement;
}
