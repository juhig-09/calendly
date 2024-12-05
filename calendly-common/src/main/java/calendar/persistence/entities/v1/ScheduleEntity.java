package calendar.persistence.entities.v1;

import calendar.model.dto.AvailableTimes;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import org.springframework.data.annotation.PersistenceCreator;

@Data
@NonFinal
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "schedules", schema = "public")
@NoArgsConstructor(force = true)
public class ScheduleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private LocalDate date;

  @Type(JsonBinaryType.class)
  @Column(name = "available_times", columnDefinition = "jsonb", nullable = false)
  private List<AvailableTimes> availableTimes;

  @Column(nullable = false)
  private String timezone;

  @Column(nullable = false, name="is_deleted")
  private boolean isDeleted;

  @Column(nullable = false, name = "created_at")
  private ZonedDateTime createdAt;

  @Column(nullable = false, name = "last_modified_at")
  private ZonedDateTime lastModifiedAt;

  @PersistenceCreator
  public ScheduleEntity(
      final String email,
      final LocalDate date,
      final List<AvailableTimes> availableTimes,
      final String timezone,
      final boolean isDeleted,
      final ZonedDateTime createdAt,
      final ZonedDateTime lastModifiedAt
  ) {
    this.email = email;
    this.date = date;
    this.availableTimes = availableTimes;
    this.timezone = timezone;
    this.isDeleted = isDeleted;
    this.createdAt = createdAt;
    this.lastModifiedAt = lastModifiedAt;
  }
}


