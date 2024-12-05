package calendar.persistence.entities.v1;

import calendar.model.enums.UserMeetingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceCreator;

@NonFinal
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "user_meetings", schema = "public")
@NoArgsConstructor(force = true)
@Data
public class UserMeetingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, name = "user_email")
  private String userEmail;

  @Column(nullable = false, name = "meeting_id")
  private Long meetingId;

  @Column(nullable = false, name = "user_status")
  private UserMeetingStatus userStatus;

  @PersistenceCreator
  public UserMeetingEntity(final Long id,
      final String userEmail,
      final Long meetingId,
      final UserMeetingStatus userStatus
  ) {
    this.id = id;
    this.userEmail = userEmail;
    this.meetingId = meetingId;
    this.userStatus = userStatus;
  }
}