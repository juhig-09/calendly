package calendar.persistence.repository;

import calendar.persistence.entities.v1.ScheduleEntity;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

  Optional<ScheduleEntity> findByEmailAndDateAndIsDeletedFalse(String email, LocalDate date);

  @Query("SELECT s FROM ScheduleEntity s WHERE s.email IN :emails and s.date = :date")
  List<ScheduleEntity> findByEmailsAndDateAndIsDeletedFalse(@Param("emails") List<String> emails, @Param("date") LocalDate date);

  @Modifying
  @Transactional
  @Query("UPDATE ScheduleEntity u SET u.isDeleted = true WHERE u.email = :email and u.date = :date")
  void softDeleteByEmailAndDate(@Param("email") String email, @Param("date") LocalDate date);

}
