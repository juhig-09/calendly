package calendar.persistence.repository;

import calendar.model.enums.UserMeetingStatus;
import calendar.persistence.entities.v1.UserMeetingEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@EnableAutoConfiguration
public interface UserMeetingRepository extends JpaRepository<UserMeetingEntity, Long> {

  List<UserMeetingEntity> findByMeetingId(Long meetingId);

  @Query("SELECT um.meetingId FROM UserMeetingEntity um WHERE um.userEmail = :userEmail")
  List<Long> findMeetingIdsByUserEmail(@Param("userEmail") String userEmail);

  @Query("SELECT um.meetingId FROM UserMeetingEntity um WHERE um.userEmail = :userEmail and um.meetingId = :meetingId")
  Long findMeetingIdByUserEmailAndMeetingId(@Param("userEmail") String userEmail, @Param("meetingId") Long meetingId);

  Optional<UserMeetingEntity> findByUserEmailAndMeetingId(String userEmail, Long meetingId);

  @Modifying
  @Transactional
  @Query("UPDATE UserMeetingEntity u SET u.userStatus = :userStatus WHERE u.userEmail = :userEmail AND u.meetingId = :meetingId")
  int updateUserStatus(@Param("userStatus") UserMeetingStatus userStatus,
      @Param("userEmail") String userEmail,
      @Param("meetingId") Long meetingId);

  @Modifying
  @Transactional
  void deleteByUserEmailAndMeetingId(String userEmail, Long meetingId);

}
