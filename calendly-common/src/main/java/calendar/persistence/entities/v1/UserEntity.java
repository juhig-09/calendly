package calendar.persistence.entities.v1;

import calendar.model.enums.UserProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceCreator;


@NonFinal
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor(force = true)
@Data
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false, name="is_deleted")
  private boolean isDeleted;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserProfile profile;

  @Column(nullable = false)
  private String timezone;

  @Column(nullable = false, name = "created_at")
  private ZonedDateTime createdAt;

  @Column(nullable = false, name = "last_modified_at")
  private ZonedDateTime lastModifiedAt;

  @PersistenceCreator
  public UserEntity(
      final String name,
      final String email,
      final boolean isDeleted,
      final UserProfile profile,
      final String timezone,
      final ZonedDateTime createdAt,
      final ZonedDateTime lastModifiedAt
  ) {
    this.name = name;
    this.email = email;
    this.isDeleted = isDeleted;
    this.profile = profile;
    this.timezone = timezone;
    this.createdAt = createdAt;
    this.lastModifiedAt = lastModifiedAt;
  }

}

