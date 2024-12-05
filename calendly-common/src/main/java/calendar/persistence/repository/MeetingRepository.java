package calendar.persistence.repository;

import calendar.persistence.entities.v1.MeetingEntity;

import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableAutoConfiguration
public interface MeetingRepository extends JpaRepository<MeetingEntity, Long> {

  List<MeetingEntity> findByIdIn(List<Long> meetingIds);

  List<MeetingEntity> findByCreatedByUserEmail(String creatorUserEmail);

  MeetingEntity findByCreatedByUserEmailAndId(String creatorUserEmail, Long id);

}
