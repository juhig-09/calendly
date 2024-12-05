package calendar.utils;

import calendar.model.dto.AvailableTimes;
import calendar.persistence.repository.UserRepository;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@AllArgsConstructor
@Service
public class Validator {

  private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
  private static final int MIN_AVAILABILITY=15;
  private static final int HOURS=24;

  public static boolean isValidEmailSyntax(final String email) {
    if (email == null || email.isEmpty()) {
      return false;
    }
    final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    final Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  public static boolean isValidUser(final UserRepository userRepository, final String email) {
    return userRepository.findByEmailAndIsDeletedFalse(email).isPresent();
  }

  public static boolean validateAvailableTimes(final List<AvailableTimes> availableTimes) {
    if (availableTimes == null || availableTimes.isEmpty() || availableTimes.size() > MIN_AVAILABILITY*HOURS) {
      return false;
    }

    for (int i = 0; i < availableTimes.size(); i++) {
      for (int j = i + 1; j < availableTimes.size(); j++) {
        final AvailableTimes currentTime = availableTimes.get(i);

        // Check if the current time interval is valid (at least 15 minutes)
        if (!isValidTimeInterval(currentTime)) {
          throw new IllegalArgumentException("User availability must be atleast " + MIN_AVAILABILITY +
              " minutes i.e; startTime and endTime should differ by atleast " + MIN_AVAILABILITY + " minutes.");
        }
        if (isOverlap(currentTime, availableTimes.get(j))) {
          throw new IllegalArgumentException("The available times must not overlap.");
        }
      }
    }
    return true;
  }

  // Method to check if two time intervals overlap
  public static boolean isOverlap(final AvailableTimes a, final AvailableTimes b) {
    return !(a.getEndTime().isBefore(b.getStartTime()) || a.getStartTime().isAfter(b.getEndTime()));
  }

  public static boolean hasOverlap(final List<AvailableTimes> availableTimes, final AvailableTimes meetingTime) {
    for (final AvailableTimes timeSlot : availableTimes) {
      if (isOverlap(timeSlot, meetingTime)) {
        return true; // Overlap found
      }
    }
    return false; // No overlap found
  }

  // Method to ensure the difference between startTime and endTime is at least MIN_AVAILABILITY
  public static boolean isValidTimeInterval(final AvailableTimes availableTimes) {
    final Duration duration = Duration.between(availableTimes.getStartTime(), availableTimes.getEndTime());
    return duration.toMinutes() >= MIN_AVAILABILITY;
  }

}
