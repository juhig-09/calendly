package calendar.api.controller.v1;

import calendar.api.service.v1.SchedulesService;
import calendar.model.dto.ScheduleDto;
import calendar.persistence.entities.v1.ScheduleEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(
    value = "/internal/v1/schedules",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@Validated
public class SchedulesController {

  private final SchedulesService schedulesService;

  @Operation
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ScheduleEntity> createSchedule(
      @Valid @RequestBody final ScheduleDto scheduleDto
  ) {
    return new ResponseEntity<>(
        schedulesService.createSchedule(scheduleDto),
        HttpStatus.CREATED);
  }

  @Operation
  @GetMapping("/users/{emailId}/dates{date}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ScheduleEntity> getSchedule(
      @PathVariable("emailId") final String email,
      @PathVariable("date") final LocalDate date
  ) {
    return new ResponseEntity<>(
        schedulesService.getSchedule(email, date),
        HttpStatus.OK);
  }

  @Operation
  @PutMapping()
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ScheduleEntity> updateSchedule(
      @Valid @RequestBody final ScheduleDto scheduleDto
  ) {
    return new ResponseEntity<>(
        schedulesService.updateSchedule(scheduleDto),
        HttpStatus.OK);
  }

  @Operation
  @DeleteMapping("/users/{emailId}/dates/{date}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteSchedule(
      @PathVariable("emailId") final String email,
      @PathVariable("date") final LocalDate date
  ) {
    schedulesService.deleteSchedule(email, date);
  }

  @ExceptionHandler()
  public ResponseEntity<Map<String, String>> handleException(final Exception ex) {
    final Map<String, String> response = new HashMap<>();
    response.put("Error:", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
