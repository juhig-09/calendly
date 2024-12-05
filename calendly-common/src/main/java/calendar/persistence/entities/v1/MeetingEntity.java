package calendar.persistence.entities.v1;

import calendar.model.enums.MeetingStatus;
import calendar.model.enums.MeetingType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceCreator;

@Data
@NonFinal
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "meetings", schema = "public")
@NoArgsConstructor(force = true)
public class MeetingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = true)
  private String description;

  @Column(nullable = false)
  private String timezone;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false, name = "start_time")
  private LocalTime startTime;

  @Column(nullable = false, name = "end_time")
  private LocalTime endTime;

  @Column(nullable = false)
  private MeetingStatus status;

  @Column(nullable = false)
  private String link;

  @Column(nullable = false, name = "meeting_type")
  @Enumerated(EnumType.STRING)
  private MeetingType meetingType;

  @Column(nullable = false, name = "optional_invitees")
  private String optionalInvitees;

  @Column(nullable = false, name = "required_invitees")
  private String requiredInvitees;

  @Column(nullable = false, name="created_by_user_email")
  private String createdByUserEmail;

  @Column(nullable = false, name = "created_at")
  private ZonedDateTime createdAt;

  @Column(nullable = false, name = "last_modified_at")
  private ZonedDateTime lastModifiedAt;

  @PersistenceCreator
  public MeetingEntity(final Long id,
      final String title,
      final String description,
      final String timezone,
      final LocalDate date,
      final LocalTime startTime,
      final LocalTime endTime,
      final MeetingStatus status,
      final String link,
      final MeetingType meetingType,
      final String optionalInvitees,
      final String requiredInvitees,
      final String createdByUserEmail,
      final ZonedDateTime createdAt,
      final ZonedDateTime lastModifiedAt
  ) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.timezone = timezone;
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.status = status;
    this.link = link;
    this.meetingType = meetingType;
    this.optionalInvitees = optionalInvitees;
    this.requiredInvitees = requiredInvitees;
    this.createdByUserEmail = createdByUserEmail;
    this.createdAt = createdAt;
    this.lastModifiedAt = lastModifiedAt;
  }

}