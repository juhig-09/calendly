package calendar.translator;

import calendar.model.dto.AvailableTimes;
import calendar.persistence.entities.v1.ScheduleEntity;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleEntityTranslator {
  public static ScheduleEntity toEntity(
      final String email,
      final LocalDate date,
      final List<AvailableTimes> availableTimes,
      final String timezone,
      final boolean isDeleted,
      final ZonedDateTime createdAt,
      final ZonedDateTime lastModifiedAt ) {
    return ScheduleEntity.builder()
        .email(email)
        .date(date)
        .availableTimes(availableTimes).timezone(timezone)
        .timezone(timezone)
        .isDeleted(isDeleted)
        .createdAt(createdAt)
        .lastModifiedAt(lastModifiedAt)
        .build();
  }

}
