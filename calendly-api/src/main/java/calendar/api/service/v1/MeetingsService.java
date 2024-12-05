package calendar.api.service.v1;

import static calendar.utils.Validator.isValidTimeInterval;

import calendar.model.dto.AvailableTimes;
import calendar.model.dto.MeetingDto;
import calendar.model.enums.MeetingStatus;
import calendar.model.enums.SupportedTimeZone;
import calendar.model.enums.UserMeetingStatus;
import calendar.persistence.entities.v1.MeetingEntity;
import calendar.persistence.entities.v1.ScheduleEntity;
import calendar.persistence.entities.v1.UserEntity;
import calendar.persistence.entities.v1.UserMeetingEntity;
import calendar.persistence.repository.MeetingRepository;
import calendar.persistence.repository.ScheduleRepository;
import calendar.persistence.repository.UserMeetingRepository;
import calendar.persistence.repository.UserRepository;
import calendar.translator.MeetingEntityTranslator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class MeetingsService {

  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;
  private final MeetingRepository meetingRepository;
  private final UserMeetingRepository userMeetingRepository;

  @Transactional
  public MeetingEntity createMeeting(final String email, final MeetingDto meetingDto) {
    String timezone = meetingDto.getTimezone();
    final UserEntity userEntity = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(() -> new EntityNotFoundException("No user found with email: " + email));
    if (!isValidTimeInterval(meetingDto.getMeetingTime())){
      throw new IllegalArgumentException("Meeting time slot must have a minimum duration of 15 minutes.");
    }
    this.validateInvitees(meetingDto);
    this.ensureRequiredInviteesAvailability(meetingDto, timezone);
    if (timezone == null) {
      timezone = userEntity.getTimezone();
    }
    final List<String> requiredInvitees = new ArrayList<>();
    if (meetingDto.getRequiredInvitees() != null){
      requiredInvitees.addAll(meetingDto.getRequiredInvitees());
    }
    final List<String> optionalInvitees = new ArrayList<>();
    if (meetingDto.getOptionalInvitees() != null){
      optionalInvitees.addAll(meetingDto.getOptionalInvitees());
    }

    final MeetingEntity meetingEntity = MeetingEntityTranslator.toEntity(
        meetingDto.getTitle(),
        meetingDto.getDescription(),
        meetingDto.getDate(),
        meetingDto.getMeetingTime().getStartTime(),
        meetingDto.getMeetingTime().getEndTime(),
        meetingDto.getStatus(),
        meetingDto.getLink(),
        meetingDto.getMeetingType(),
        String.join(",", optionalInvitees),
        String.join(",", requiredInvitees),
        email,
        ZonedDateTime.now(ZoneId.of("UTC")),
        ZonedDateTime.now(ZoneId.of("UTC")),
        timezone
    );
    meetingRepository.save(meetingEntity);
    this.saveUserMeetings(meetingEntity);
    return meetingEntity;
  }

  public MeetingEntity getMeeting(final Long meetingId) {
    return meetingRepository.findById(meetingId).orElseThrow(() -> new EntityNotFoundException("No meeting found with id: " + meetingId));
  }

  @Transactional
  public MeetingEntity updateMeeting(final String email, final Long meetingId, final MeetingDto meetingDto) {
    String timezone = meetingDto.getTimezone();
    final UserEntity userEntity = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(() -> new EntityNotFoundException("No user found with email: " + email));
    if (!isValidTimeInterval(meetingDto.getMeetingTime())){
      throw new IllegalArgumentException("Meeting time slot must have a minimum duration of 15 minutes.");
    }
    this.validateInvitees(meetingDto);
    this.ensureRequiredInviteesAvailability(meetingDto, timezone);
    if (timezone == null) {
      timezone = userEntity.getTimezone();
    }
    final List<String> requiredInvitees = new ArrayList<>();
    if (meetingDto.getRequiredInvitees() != null){
      requiredInvitees.addAll(meetingDto.getRequiredInvitees());
    }
    final List<String> optionalInvitees = new ArrayList<>();
    if (meetingDto.getOptionalInvitees() != null){
      optionalInvitees.addAll(meetingDto.getOptionalInvitees());
    }

    final MeetingEntity existingMeeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("Meeting with ID " + meetingId + " not found"));

    existingMeeting.setTitle(meetingDto.getTitle());
    existingMeeting.setDescription(meetingDto.getDescription());
    existingMeeting.setDate(meetingDto.getDate());
    existingMeeting.setStartTime(meetingDto.getMeetingTime().getStartTime());
    existingMeeting.setEndTime(meetingDto.getMeetingTime().getEndTime());
    existingMeeting.setStatus(MeetingStatus.SCHEDULED);
    existingMeeting.setLink(meetingDto.getLink());
    existingMeeting.setMeetingType(meetingDto.getMeetingType());
    existingMeeting.setOptionalInvitees(String.join(",", optionalInvitees));
    existingMeeting.setRequiredInvitees(String.join(",", requiredInvitees));
    existingMeeting.setCreatedByUserEmail(email);
    existingMeeting.setLastModifiedAt(ZonedDateTime.now(ZoneId.of("UTC")));
    existingMeeting.setTimezone(timezone);
    meetingRepository.save(existingMeeting);
    this.updateUserMeetings(existingMeeting);
    this.saveUserMeetings(existingMeeting);
    return existingMeeting;
  }

  private void validateInvitees(final MeetingDto meetingDto) {
    final List<String> requiredInvitees = new ArrayList<>();
    if (meetingDto.getRequiredInvitees() != null) {
      requiredInvitees.addAll(meetingDto.getRequiredInvitees());
    }
    final List<String> optionalInvitees = new ArrayList<>();
    if (meetingDto.getOptionalInvitees() != null) {
      optionalInvitees.addAll(meetingDto.getOptionalInvitees());
    }
    final List<String> invitees = new ArrayList<>();
    invitees.addAll(requiredInvitees);
    invitees.addAll(optionalInvitees);
    if (invitees.isEmpty()){
      throw new IllegalArgumentException("The meeting must have one invitee.");
    }
    final List<String> tempInvitees = new ArrayList<>(requiredInvitees);
    tempInvitees.retainAll(optionalInvitees);
    if (!tempInvitees.isEmpty()) {
      throw new IllegalArgumentException("The required and optional invitees must be disjoint.");
    }

    for (final String inviteeEmail : invitees) {
      final UserEntity user = userRepository.findByEmailAndIsDeletedFalse(inviteeEmail).orElse(null);
      if (user == null) {
        throw new IllegalArgumentException("This user doesn't exist: " + inviteeEmail);
      }
    }
  }

  private void ensureRequiredInviteesAvailability(final MeetingDto meetingDto, final String timezone) {
    if (meetingDto.getRequiredInvitees() != null) {
      final List<ScheduleEntity> requiredUsersSchedule = scheduleRepository.findByEmailsAndDateAndIsDeletedFalse(meetingDto.getRequiredInvitees(), meetingDto.getDate());
      final List<ScheduleEntity> unAvailableRequiredInvitees = requiredUsersSchedule.stream()
              .filter(schedule -> {

                // Convert meeting times to UTC
                final ZonedDateTime meetingStartUTC = convertToUTC(meetingDto.getMeetingTime().getStartTime(), meetingDto.getDate(), timezone);
                final ZonedDateTime meetingEndUTC = convertToUTC(meetingDto.getMeetingTime().getEndTime(), meetingDto.getDate(), timezone);

                return schedule.getAvailableTimes().stream()
                        .noneMatch(timeSlot -> {
                          // Convert schedule's timeSlot to UTC
                          final ZonedDateTime slotStartUTC = convertToUTC(timeSlot.getStartTime(), schedule.getDate(), schedule.getTimezone());
                          final ZonedDateTime slotEndUTC = convertToUTC(timeSlot.getEndTime(), schedule.getDate(), schedule.getTimezone());

                          // Check for overlap in UTC
                          return (slotStartUTC.isBefore(meetingStartUTC) || slotStartUTC.isEqual(meetingStartUTC))
                              && (slotEndUTC.isAfter(meetingEndUTC) || slotEndUTC.isEqual(meetingEndUTC));
                        });
              })
              .toList();

      if (!unAvailableRequiredInvitees.isEmpty()) {
        final Map<String, List<AvailableTimes>> unAvailableUsers = unAvailableRequiredInvitees.stream()
                .collect(Collectors.toMap(
                        ScheduleEntity::getEmail,            // Key: email
                        ScheduleEntity::getAvailableTimes   // Value: List<AvailableTimes>
                ));
        throw new IllegalArgumentException("The meeting cannot be scheduled as one or more required invitees are not available at the selected time. "
                + "Following is the availability of required invitees on " + meetingDto.getDate() + " : "
                + unAvailableUsers.toString());
      }
    }
  }

  private ZonedDateTime convertToUTC(final LocalTime time, final LocalDate date, final String timezone) {
    final ZoneId zoneId = SupportedTimeZone.getZoneIdFromAbbreviation(timezone);
    return time.atDate(date)
            .atZone(zoneId)
            .withZoneSameInstant(ZoneId.of("UTC"));
  }

  @Transactional
  protected void saveUserMeetings(final MeetingEntity meetingEntity) {
    final List<String> invitees = new ArrayList<>();
    if (meetingEntity.getRequiredInvitees() != null && !meetingEntity.getRequiredInvitees().isEmpty()) {
      invitees.addAll(Arrays.asList(meetingEntity.getRequiredInvitees().split(",")));
    }
    if (meetingEntity.getOptionalInvitees() != null && !meetingEntity.getOptionalInvitees().isEmpty()) {
      invitees.addAll(Arrays.asList(meetingEntity.getOptionalInvitees().split(",")));
    }
    for (final String inviteeEmail : invitees) {
      final UserEntity user = userRepository.findByEmailAndIsDeletedFalse(inviteeEmail).orElse(null);
      if (user != null) {
        final UserMeetingEntity userMeetingEntity = new UserMeetingEntity(
            null,
            user.getEmail(),
            meetingEntity.getId(),
            UserMeetingStatus.PENDING
        );
        userMeetingRepository.save(userMeetingEntity);
      }
    }
  }

  @Transactional
  protected void updateUserMeetings(final MeetingEntity meetingEntity) {
    final List<UserMeetingEntity> userMeetingEntities = userMeetingRepository.findByMeetingId(meetingEntity.getId());

    final List<String> existingInvitees = userMeetingEntities.stream()
        .map(UserMeetingEntity::getUserEmail)
        .collect(Collectors.toList());

    final List<String> newInvitees = List.of(meetingEntity.getRequiredInvitees().split(","));
    existingInvitees.addAll(List.of(meetingEntity.getOptionalInvitees().split(",")));

    final List<String> inviteesToRemove = new ArrayList<>(existingInvitees);
    inviteesToRemove.removeAll(newInvitees);

    for (final String removeInviteeEmail : inviteesToRemove) {
        userMeetingRepository.deleteByUserEmailAndMeetingId(removeInviteeEmail, meetingEntity.getId());
      }
    }
}

