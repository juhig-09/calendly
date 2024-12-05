package calendar.model.dto;

import calendar.model.enums.UserProfile;
import calendar.model.validation.groups.CreateUser;
import calendar.model.validation.groups.PatchUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Builder(toBuilder = true)
public class UserSignupDto {

  @Valid
  @NotEmpty(message = "Name cannot be empty", groups = {CreateUser.class})
  @Schema(description = "Name of the User being created.",
      nullable = false)
  private final String name;

  @Valid
  @NotEmpty(message = "EmailId cannot be empty", groups = {CreateUser.class, PatchUser.class})
  @Schema(description = "EmailId of the User being created.",
      nullable = false)
  private final String email;

  @Valid
  @Schema(description = "User profile - PRIVATE | PUBLIC. Default is PUBLIC for ease of use",
      nullable = true)
  private final UserProfile profile;

  @Valid
  @Schema(description = "TimeZone of the user. Default is IST",
      nullable = true)
  private final String timezone;

}