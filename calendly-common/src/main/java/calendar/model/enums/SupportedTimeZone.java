package calendar.model.enums;

import java.time.ZoneId;

public enum SupportedTimeZone {
  ACT("Australia/Darwin"),
  AET("Australia/Sydney"),
  AGT("America/Argentina/Buenos_Aires"),
  ART("Africa/Cairo"),
  AST("America/Anchorage"),
  BET("America/Sao_Paulo"),
  BST("Asia/Dhaka"),
  CAT("Africa/Harare"),
  CNT("America/St_Johns"),
  CST("America/Chicago"),
  CTT("Asia/Shanghai"),
  EAT("Africa/Addis_Ababa"),
  ECT("Europe/Paris"),
  IET("America/Indiana/Indianapolis"),
  IST("Asia/Kolkata"),
  JST("Asia/Tokyo"),
  MIT("Pacific/Apia"),
  NET("Asia/Yerevan"),
  NST("Pacific/Auckland"),
  PLT("Asia/Karachi"),
  PNT("America/Phoenix"),
  PRT("America/Puerto_Rico"),
  PST("America/Los_Angeles"),
  SST("Pacific/Guadalcanal"),
  VST("Asia/Ho_Chi_Minh"),
  EST("Etc/GMT-5"),
  MST("Etc/GMT-7"),
  HST("Etc/GMT-10");

  private final String zoneId;

  SupportedTimeZone(final String zoneId) {
    this.zoneId = zoneId;
  }

  public String getZoneId() {
    return zoneId;
  }

  public static ZoneId getZoneIdFromAbbreviation(final String abbreviation) {
    for (SupportedTimeZone tz : values()) {
      if (tz.name().equalsIgnoreCase(abbreviation)) {
        return ZoneId.of(tz.zoneId);
      }
    }
    throw new IllegalArgumentException("Unknown time-zone ID: " + abbreviation);
  }
}
