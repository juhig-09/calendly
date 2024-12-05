package calendar.translator;

import calendar.model.enums.UserProfile;
import calendar.persistence.entities.v1.UserEntity;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityTranslator {

  public static UserEntity toEntity(
      final String name,
      final String email,
      final boolean isDeleted,
      final UserProfile profile,
      final String timezone,
      final ZonedDateTime createdAt,
      final ZonedDateTime lastModifiedAt) {
    return UserEntity.builder()
        .name(name)
        .email(email)
        .isDeleted(isDeleted)
        .profile(profile)
        .timezone(timezone)
        .createdAt(createdAt)
        .lastModifiedAt(lastModifiedAt)
        .build();
  }
}

