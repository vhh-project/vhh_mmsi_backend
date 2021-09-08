package eu.vhhproject.mmsi.shotservice.mapping;

import java.util.List;

import org.mapstruct.Mapper;

import eu.vhhproject.mmsi.shotservice.api.tos.ShotTO;
import eu.vhhproject.mmsi.shotservice.model.Shot;

@Mapper
public abstract class ShotMapper {

  public abstract Shot shotTOToShot(ShotTO shotTO);

  public abstract List<Shot> shotTOsToShots(List<ShotTO> shotTOs);

  public abstract ShotTO shotToTOShot(Shot shot);

  public abstract List<ShotTO> shotsToShotTOs(List<Shot> shots);
}
