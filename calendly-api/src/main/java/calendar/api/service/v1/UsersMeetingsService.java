package calendar.api.service.v1;

import calendar.model.dto.AvailableTimes;
import calendar.model.enums.SupportedTimeZone;
import calendar.model.enums.UserMeetingStatus;
import calendar.persistence.entities.v1.MeetingEntity;
import calendar.persistence.entities.v1.ScheduleEntity;
import calendar.persistence.entities.v1.UserMeetingEntity;
import calendar.persistence.repository.MeetingRepository;
import calendar.persistence.repository.ScheduleRepository;
import calendar.persistence.repository.UserMeetingRepository;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import calendar.persistence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static calendar.utils.Convertor.toUtc;
import static calendar.utils.Validator.*;

@Slf4j
@AllArgsConstructor
@Service
public class UsersMeetingsService {

  private final UserMeetingRepository userMeetingRepository;
  private final MeetingRepository meetingRepository;
  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;

  public List<MeetingEntity> getUserMeetings(final String userEmail) {
    final List<MeetingEntity> userAsInviteeOrCreator = new ArrayList<MeetingEntity>();
    if (!isValidEmailSyntax(userEmail)) {
      throw new IllegalArgumentException("Invalid email address.");
    }
    userRepository.findByEmailAndIsDeletedFalse(userEmail)
        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));
    final List<Long> meetingIds = userMeetingRepository.findMeetingIdsByUserEmail(userEmail);
    if (!meetingIds.isEmpty()) {
      userAsInviteeOrCreator.addAll(meetingRepository.findByIdIn(meetingIds));
    }
    userAsInviteeOrCreator.addAll(meetingRepository.findByCreatedByUserEmail(userEmail));
    return userAsInviteeOrCreator;
  }

  public MeetingEntity getUserMeetingById(final String userEmail, final Long meetingId) {
    if (!isValidEmailSyntax(userEmail)) {
      throw new IllegalArgumentException("Invalid email address.");
    }
    userRepository.findByEmailAndIsDeletedFalse(userEmail)
        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));
    final Long existingMeetingId = userMeetingRepository.findMeetingIdByUserEmailAndMeetingId(userEmail, meetingId);
    if (existingMeetingId == null) {
      final MeetingEntity userAsInviteeOrCreator = meetingRepository.findByCreatedByUserEmailAndId(userEmail, meetingId);
      if (userAsInviteeOrCreator == null) {
        throw new IllegalArgumentException("No such meeting exists for this user.");
      }
      return userAsInviteeOrCreator;
    }
    return meetingRepository.findById(existingMeetingId).orElseThrow(() -> new EntityNotFoundException("No such meeting found for a user with id: " + userEmail));
  }

  public MeetingEntity updateUserMeetingStatus(final String userEmail, final Long meetingId, final
      UserMeetingStatus status) {
    if (!isValidEmailSyntax(userEmail)) {
      throw new IllegalArgumentException("Invalid email address.");
    }
    userRepository.findByEmailAndIsDeletedFalse(userEmail)
        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));
    final UserMeetingEntity existingUserMeetingEntity = userMeetingRepository.findByUserEmailAndMeetingId(userEmail, meetingId).orElse(null);
    final MeetingEntity meetingEntity = meetingRepository.findById(meetingId).orElseThrow(() -> new EntityNotFoundException("No such meeting found for a user with id: " + userEmail));
    final ScheduleEntity scheduleEntity = scheduleRepository.findByEmailAndDateAndIsDeletedFalse(userEmail, meetingEntity.getDate()).orElseThrow(() ->
        new EntityNotFoundException("No schedule found for a user with id: " + userEmail + " for the date: " + meetingEntity.getDate()));

    if (status == UserMeetingStatus.ACCEPTED &&
      !hasOverlap(scheduleEntity.getAvailableTimes(),
          new AvailableTimes(meetingEntity.getStartTime(), meetingEntity.getEndTime()))) {
      throw new IllegalArgumentException("Failed to accept the meeting as the availability does not match with meeting time.");
    }
    final int updated = userMeetingRepository.updateUserStatus(status, userEmail, meetingId);
    if (updated == 0) {
      throw new IllegalArgumentException("No such meeting exists for this user.");
    }
    //Update the user availability if user accepts a meeting
    if (status == UserMeetingStatus.ACCEPTED){
        scheduleEntity.setAvailableTimes(removeOverlappingTime(meetingEntity, scheduleEntity));
        scheduleEntity.setLastModifiedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        scheduleRepository.save(scheduleEntity);
    }

    //Update the user availability if user rejects a meeting previously accepted
    if (status == UserMeetingStatus.REJECTED &&
        existingUserMeetingEntity != null &&
        existingUserMeetingEntity.getUserStatus() == UserMeetingStatus.ACCEPTED) {
        scheduleEntity.setAvailableTimes(addBackCommonTimeAndMerge(meetingEntity, scheduleEntity));
        scheduleEntity.setLastModifiedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        scheduleRepository.save(scheduleEntity);
    }
    return meetingEntity;
  }

  public List<AvailableTimes> removeOverlappingTime(final MeetingEntity meetingEntity, final ScheduleEntity scheduleEntity) {
    final ZoneId meetingZoneId = SupportedTimeZone.getZoneIdFromAbbreviation(meetingEntity.getTimezone());
    final ZoneId scheduleZoneId = SupportedTimeZone.getZoneIdFromAbbreviation(scheduleEntity.getTimezone());

    // Convert the meeting's start and end time to UTC
    final ZonedDateTime meetingStartUTC = ZonedDateTime.of(meetingEntity.getStartTime().atDate(meetingEntity.getDate()), meetingZoneId).withZoneSameInstant(ZoneId.of("UTC"));
    final ZonedDateTime meetingEndUTC = ZonedDateTime.of(meetingEntity.getEndTime().atDate(meetingEntity.getDate()), meetingZoneId).withZoneSameInstant(ZoneId.of("UTC"));

    // Create a new list for available times that will be modified
    final List<AvailableTimes> updatedAvailableTimes = new ArrayList<>();

    // Loop through available times and modify/remove overlaps
    for (final AvailableTimes availableTime : scheduleEntity.getAvailableTimes()) {
      final ZonedDateTime slotStartUTC = ZonedDateTime.of(availableTime.getStartTime().atDate(scheduleEntity.getDate()), scheduleZoneId).withZoneSameInstant(ZoneId.of("UTC"));
      final ZonedDateTime slotEndUTC = ZonedDateTime.of(availableTime.getEndTime().atDate(scheduleEntity.getDate()), scheduleZoneId).withZoneSameInstant(ZoneId.of("UTC"));

      // If there's an overlap, split the slot into two parts
      if (slotStartUTC.isBefore(meetingEndUTC) && slotEndUTC.isAfter(meetingStartUTC)) {
        // Part of the slot before the meeting time
        if (slotStartUTC.isBefore(meetingStartUTC)) {
          updatedAvailableTimes.add(new AvailableTimes(slotStartUTC.toLocalTime(), meetingStartUTC.toLocalTime()));
        }

        // Part of the slot after the meeting time
        if (slotEndUTC.isAfter(meetingEndUTC)) {
          updatedAvailableTimes.add(new AvailableTimes(meetingEndUTC.toLocalTime(), slotEndUTC.toLocalTime()));
        }
      }
      // No overlap, keep the original slot
      else {
        updatedAvailableTimes.add(availableTime);
      }
    }
    // Replace the original available times with the modified ones
    return updatedAvailableTimes;
  }

  private List<AvailableTimes> addBackCommonTimeAndMerge(
      final MeetingEntity meetingEntity, final ScheduleEntity scheduleEntity
  ) {
    // Step 1: Normalize both the meeting time and available time slots to UTC
    final ZonedDateTime meetingStartUTC = toUtc(meetingEntity.getStartTime(), meetingEntity.getTimezone());
    final ZonedDateTime meetingEndUTC = toUtc(meetingEntity.getEndTime(), meetingEntity.getTimezone());

    // Step 2: Add back the meeting time
    List<AvailableTimes> updatedAvailableTimes = new ArrayList<>(scheduleEntity.getAvailableTimes());
    updatedAvailableTimes.add(new AvailableTimes(meetingEntity.getStartTime(), meetingEntity.getEndTime()));

    // Step 3: Merge overlapping or adjacent time slots
    updatedAvailableTimes = mergeOverlappingTimeSlots(updatedAvailableTimes, scheduleEntity.getTimezone());

    // Step 4: Return the updated and merged list
    return updatedAvailableTimes;
  }

  // Helper method to merge overlapping or adjacent time slots
  private List<AvailableTimes> mergeOverlappingTimeSlots(
      final List<AvailableTimes> availableTimes,
      final String scheduleTimezone
  ) {

    // Sort the time slots based on start time
    availableTimes.sort(Comparator.comparing(AvailableTimes::getStartTime));

    final List<AvailableTimes> mergedTimeSlots = new ArrayList<>();
    AvailableTimes currentSlot = null;

    for (final AvailableTimes slot : availableTimes) {
      final ZonedDateTime slotStartUTC = toUtc(slot.getStartTime(), scheduleTimezone);
      final ZonedDateTime slotEndUTC = toUtc(slot.getEndTime(), scheduleTimezone);

      if (currentSlot == null) {
        currentSlot = slot;
      } else {
        final ZonedDateTime currentSlotEndUTC = toUtc(currentSlot.getEndTime(), scheduleTimezone);

        // If the current slot overlaps or is adjacent to the next slot, merge them
        if (currentSlotEndUTC.isAfter(slotStartUTC) || currentSlotEndUTC.isEqual(slotStartUTC)) {
          // Merge the slots: extend the current slot's end time
          currentSlot = new AvailableTimes(
              currentSlot.getStartTime(),
              currentSlotEndUTC.isAfter(slotEndUTC) ? currentSlot.getEndTime() : slot.getEndTime()
          );
        } else {
          // No overlap, add the current slot to the merged list
          mergedTimeSlots.add(currentSlot);
          currentSlot = slot;
        }
      }
    }
    // Add the last slot
    if (currentSlot != null) {
      mergedTimeSlots.add(currentSlot);
    }
    return mergedTimeSlots;
  }

}

