package calendar.api.controller.v1;

import calendar.api.service.v1.UsersMeetingsService;
import calendar.model.enums.UserMeetingStatus;
import calendar.persistence.entities.v1.MeetingEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(
    value = "/internal/v1/user-meetings",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@Validated
public class UsersMeetingsController {

  private final UsersMeetingsService usersMeetingsService;

  @Operation
  @GetMapping("/users/{userEmail}/meetings")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MeetingEntity>> getUserMeetings(
      @PathVariable("userEmail") final String userEmail
  ) {
    return new ResponseEntity<>(
        usersMeetingsService.getUserMeetings(userEmail),
        HttpStatus.OK);
  }

  @Operation
  @GetMapping("/users/{userEmail}/meetings/{meetingId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MeetingEntity> getUserMeetingById(
      @PathVariable("userEmail") final String userEmail,
      @PathVariable("meetingId") final Long meetingId
  ) {
    return new ResponseEntity<>(
        usersMeetingsService.getUserMeetingById(userEmail, meetingId),
        HttpStatus.OK);
  }

  @Operation
  @PatchMapping("/users/{userEmail}/meetings/{meetingId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MeetingEntity> updateUserMeetingStatus(
      @PathVariable("userEmail") final String userEmail,
      @PathVariable("meetingId") final Long meetingId,
      @Valid @RequestBody final UserMeetingStatus userMeetingStatus
  ) {
    return new ResponseEntity<>(
        usersMeetingsService.updateUserMeetingStatus(userEmail, meetingId, userMeetingStatus),
        HttpStatus.OK);
  }

  @ExceptionHandler()
  public ResponseEntity<Map<String, String>> handleException(final Exception ex) {
    final Map<String, String> response = new HashMap<>();
    response.put("Error:", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

}
