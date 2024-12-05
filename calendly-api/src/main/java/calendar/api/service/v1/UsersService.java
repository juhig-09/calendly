package calendar.api.service.v1;

import static calendar.utils.Validator.isValidEmailSyntax;

import calendar.model.dto.UserSignupDto;
import calendar.model.enums.UserProfile;
import calendar.persistence.entities.v1.UserEntity;
import calendar.persistence.repository.UserRepository;
import calendar.translator.UserEntityTranslator;
import jakarta.persistence.EntityNotFoundException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class UsersService {

  private final UserRepository userRepository;

  @Transactional
  public UserEntity createUser(final UserSignupDto userSignupDto) {
    if (userSignupDto.getName() == null || userSignupDto.getName().isEmpty() ||
    userSignupDto.getEmail() == null || userSignupDto.getEmail().isEmpty()) {
      throw new IllegalArgumentException("User name and email is required.");
    }
    if (!isValidEmailSyntax(userSignupDto.getEmail())) {
      throw new IllegalArgumentException("Invalid email address.");
    }
    String timeZone= userSignupDto.getTimezone();
    if (userSignupDto.getTimezone() == null){
      timeZone = "IST";
    }

    UserProfile userProfile = userSignupDto.getProfile();
    if (userProfile == null){
      userProfile = UserProfile.PUBLIC;
    }
    final UserEntity userEntity = UserEntityTranslator.toEntity(
        userSignupDto.getName(),
        userSignupDto.getEmail(),
        false,
        userProfile,
        timeZone,
        ZonedDateTime.now(ZoneId.of("UTC")),
        ZonedDateTime.now(ZoneId.of("UTC"))
    );
    return userRepository.save(userEntity);
  }

  public UserEntity getUser(final String email) {
    if (!isValidEmailSyntax(email)) {
      throw new IllegalArgumentException("Invalid email address.");
    }
    return userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(() -> new EntityNotFoundException("No active user found with email: " + email) );
  }

  @Transactional
  public UserEntity updateUser(final UserSignupDto userSignupDto) {
    UserEntity user = userRepository.findByEmailAndIsDeletedFalse(userSignupDto.getEmail())
        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userSignupDto.getEmail()));

    // Update fields if they are provided
    if (userSignupDto.getName() != null && !userSignupDto.getName().isEmpty()) {
      user.setName(userSignupDto.getName());
    }

    if (userSignupDto.getProfile() != null) {
      user.setProfile(userSignupDto.getProfile());
    }

    if (userSignupDto.getTimezone() != null && !userSignupDto.getTimezone().isEmpty()) {
      user.setTimezone(userSignupDto.getTimezone());
    }
    user.setLastModifiedAt(ZonedDateTime.now(ZoneId.of("UTC")));

    return userRepository.save(user);
  }

  @Transactional
  public void deleteUser(final String email) {
    if (!isValidEmailSyntax(email)) {
      throw new IllegalArgumentException("Invalid email address.");
    }
    userRepository.softDeleteByEmail(email);
  }

}
