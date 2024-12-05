package calendar.utils;

import calendar.model.enums.SupportedTimeZone;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Convertor {

  // Helper method to convert to UTC
  public static ZonedDateTime toUtc(final LocalTime localTime, final String timezone) {
    final ZoneId zoneId = SupportedTimeZone.getZoneIdFromAbbreviation(timezone);
    final ZonedDateTime zonedDateTime = localTime.atDate(java.time.LocalDate.now()).atZone(zoneId);
    return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
  }
}
