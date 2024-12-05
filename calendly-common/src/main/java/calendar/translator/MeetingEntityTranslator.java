package calendar.translator;

import calendar.model.enums.MeetingStatus;
import calendar.model.enums.MeetingType;
import calendar.persistence.entities.v1.MeetingEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MeetingEntityTranslator {

  public static MeetingEntity toEntity(
      final String title,
      final String description,
      final LocalDate date,
      final LocalTime startTime,
      final LocalTime endTime,
      final MeetingStatus status,
      final String link,
      final MeetingType meetingType,
      final String optionalInvitees,
      final String requiredInvitees,
      final String userEmail,
      final ZonedDateTime createdAt,
      final ZonedDateTime lastModifiedAt,
      final String timezone) {
    return MeetingEntity.builder()
        .title(title)
        .description(description)
        .date(date)
        .startTime(startTime)
        .endTime(endTime)
        .status(status)
        .link(link)
        .meetingType(meetingType)
        .optionalInvitees(optionalInvitees)
        .requiredInvitees(requiredInvitees)
        .createdByUserEmail(userEmail)
        .createdAt(createdAt)
        .lastModifiedAt(lastModifiedAt)
        .timezone(timezone)
        .build();
  }

}
