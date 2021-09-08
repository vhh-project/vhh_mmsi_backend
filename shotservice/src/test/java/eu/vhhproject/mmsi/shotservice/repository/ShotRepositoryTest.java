package eu.vhhproject.mmsi.shotservice.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.vhhproject.mmsi.shotservice.model.Shot;

@SpringBootTest
@Transactional
public class ShotRepositoryTest {

  @Autowired
  private ShotRepository shotRepository;

  @Test
  void testShotSave()
  {
    Long numShotsBefore = shotRepository.count();

    Shot shot = new Shot();

    shot.setVideoId(1L);
    shot.setInPoint(1);
    shot.setOutPoint(100);

    shotRepository.save(shot);

    assertThat(shotRepository.count(), is(numShotsBefore + 1L));
  }
}
