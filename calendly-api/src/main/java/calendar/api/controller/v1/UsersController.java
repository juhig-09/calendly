package calendar.api.controller.v1;

import calendar.model.dto.UserSignupDto;
import calendar.api.service.v1.UsersService;
import calendar.model.validation.groups.PatchUser;
import calendar.persistence.entities.v1.UserEntity;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(
    value = "/internal/v1/users",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@Validated
public class UsersController {

  private final UsersService usersService;

  @Operation
  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<UserEntity> createUser(
      @Valid @RequestBody final UserSignupDto userSignupDto
  ) {
    return new ResponseEntity<>(
        usersService.createUser(userSignupDto),
        HttpStatus.CREATED);
  }

  @Operation
  @GetMapping("/{email}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<UserEntity> getUser(
      @PathVariable("email") final String email
  ) {
    return new ResponseEntity<>(
        usersService.getUser(email),
        HttpStatus.OK);
  }

  @Operation
  @PatchMapping("/{email}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<UserEntity> updateUser(
      @PathVariable("email") final String email,
      @Validated({PatchUser.class})
      @Valid @RequestBody final UserSignupDto userSignupDto
  ) {
    return new ResponseEntity<>(
        usersService.updateUser(userSignupDto),
        HttpStatus.OK);
  }

  @Operation
  @DeleteMapping("/{email}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(
      @PathVariable("email") final String email
  ) {
        usersService.deleteUser(email);
  }

  @ExceptionHandler()
  public ResponseEntity<Map<String, String>> handleException(final Exception ex) {
    final Map<String, String> response = new HashMap<>();
    response.put("Error:", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
