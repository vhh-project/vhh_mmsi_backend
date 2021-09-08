package eu.vhhproject.mmsi.shotservice.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import eu.vhhproject.mmsi.shotservice.model.Shot;

public interface ShotRepository extends JpaRepository<Shot, Long> {

  public List<Shot> findByVideoId(Long videoId, Sort sort);

  public boolean existsByVideoId(Long videoId);

  public void deleteByVideoId(Long videoId);
}
