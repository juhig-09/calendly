package calendar.api.controller.v1;

import calendar.api.service.v1.MeetingsService;
import calendar.model.dto.MeetingDto;
import calendar.persistence.entities.v1.MeetingEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(
    value = "/internal/v1/meetings",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@Validated
public class MeetingsController {

  private final MeetingsService meetingService;

  @Operation
  @PostMapping("/{email}")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<MeetingEntity> createMeeting(
      @PathVariable("email") final String email,
      @Valid @RequestBody final MeetingDto meetingDto
  ) {
    return new ResponseEntity<>(
        meetingService.createMeeting(email, meetingDto),
        HttpStatus.CREATED);
  }

  @Operation
  @GetMapping("/{meetingId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MeetingEntity> getMeeting(
      @PathVariable("meetingId") final Long meetingId
  ) {
    return new ResponseEntity<>(
        meetingService.getMeeting(meetingId),
        HttpStatus.OK);
  }

  @Operation
  @PutMapping("/{emailId}/{meetingId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MeetingEntity> updateMeeting(
      @PathVariable("emailId") final String email,
      @PathVariable("meetingId") final Long meetingId,
      @Valid @RequestBody final MeetingDto meetingDto
  ) {
    return new ResponseEntity<>(
        meetingService.updateMeeting(email, meetingId, meetingDto),
        HttpStatus.OK);
  }

  @ExceptionHandler()
  public ResponseEntity<Map<String, String>> handleException(final Exception ex) {
    final Map<String, String> response = new HashMap<>();
    response.put("Error:", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

}
