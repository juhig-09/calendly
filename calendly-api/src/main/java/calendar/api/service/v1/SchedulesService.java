package calendar.api.service.v1;


import static calendar.utils.Validator.isValidUser;
import static calendar.utils.Validator.validateAvailableTimes;

import calendar.model.dto.ScheduleDto;
import calendar.persistence.entities.v1.ScheduleEntity;
import calendar.persistence.entities.v1.UserEntity;
import calendar.persistence.repository.ScheduleRepository;
import calendar.persistence.repository.UserRepository;
import calendar.translator.ScheduleEntityTranslator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@AllArgsConstructor
@Service
public class SchedulesService {

  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;

  @Transactional
  public ScheduleEntity createSchedule(final ScheduleDto scheduleDto) {
    if ( scheduleDto.getAvailableTimes().isEmpty()) {
      throw new IllegalArgumentException("User available times are required.");
    }
    if (!isValidUser(userRepository, scheduleDto.getEmail() )){
      throw new IllegalArgumentException("User email is not valid. This user doesn't exist.");
    }
    if (!validateAvailableTimes(scheduleDto.getAvailableTimes())){
      throw new IllegalArgumentException("User availability time slots must have a minimum duration of 15 minutes. "
          + "The total number of availability slots cannot exceed 96 for a single day (24 hours with 15-minute intervals).");
    }
    String timezone = scheduleDto.getTimezone();
    if (timezone == null) {
      final UserEntity userEntity = userRepository.findByEmailAndIsDeletedFalse(scheduleDto.getEmail()).orElseThrow(() -> new EntityNotFoundException("No user found with email: " + scheduleDto.getEmail()));;
      timezone = userEntity.getTimezone();
    }
    final ScheduleEntity scheduleEntity = ScheduleEntityTranslator.toEntity(
        scheduleDto.getEmail(),
        scheduleDto.getDate(),
        scheduleDto.getAvailableTimes(),
        timezone,
        false,
        ZonedDateTime.now(ZoneId.of("UTC")),
        ZonedDateTime.now(ZoneId.of("UTC")));
    return scheduleRepository.save(scheduleEntity);
  }

  public ScheduleEntity getSchedule(final String email, final LocalDate date) {
    if (!isValidUser(userRepository, email )){
      throw new IllegalArgumentException("User email is not valid. This user doesn't exist.");
    }
    return scheduleRepository.findByEmailAndDateAndIsDeletedFalse(email, date).orElseThrow(() -> new EntityNotFoundException("No schedule found for a user with email: " + email));
  }

  @Transactional
  public ScheduleEntity updateSchedule(final ScheduleDto scheduleDto) {
    if (scheduleDto.getAvailableTimes().isEmpty()) {
      throw new IllegalArgumentException("User available times are required.");
    }
    if (!isValidUser(userRepository, scheduleDto.getEmail() )){
      throw new IllegalArgumentException("User email is not valid. This user doesn't exist.");
    }
    if (!validateAvailableTimes(scheduleDto.getAvailableTimes())){
      throw new IllegalArgumentException("User availability time slots must have a minimum duration of 15 minutes. "
          + "The total number of availability slots cannot exceed 96 for a single day (24 hours with 15-minute intervals).");
    }
    String timezone = scheduleDto.getTimezone();
    if (timezone == null) {
      final UserEntity userEntity = userRepository.findByEmailAndIsDeletedFalse(scheduleDto.getEmail()).orElseThrow(() -> new EntityNotFoundException("No user found with email: " + scheduleDto.getEmail()));;
      timezone = userEntity.getTimezone();
    }
    final ScheduleEntity scheduleEntity = scheduleRepository.findByEmailAndDateAndIsDeletedFalse(scheduleDto.getEmail(), scheduleDto.getDate()).orElse(null);
    if (scheduleEntity != null) {
      scheduleEntity.setAvailableTimes(scheduleDto.getAvailableTimes());
      scheduleEntity.setTimezone(timezone);
      scheduleEntity.setLastModifiedAt(ZonedDateTime.now(ZoneId.of("UTC")));
    }
    else {
      throw new IllegalArgumentException("Update Failed: No schedule found for the given email and date.");
    }
    return scheduleRepository.findByEmailAndDateAndIsDeletedFalse(scheduleDto.getEmail(), scheduleDto.getDate()).orElseThrow(() -> new EntityNotFoundException("No schedule found for a user with email: " + scheduleDto.getEmail()));
  }

  @Transactional
  public void deleteSchedule(final String email, final LocalDate date) {
    if (!isValidUser(userRepository, email )){
      throw new IllegalArgumentException("User email is not valid. This user doesn't exist.");
    }
    scheduleRepository.softDeleteByEmailAndDate(email, date);
  }

}
