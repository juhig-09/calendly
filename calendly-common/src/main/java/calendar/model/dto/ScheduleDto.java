package calendar.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Builder(toBuilder = true)
public class ScheduleDto {

  @Valid
  @NotEmpty(message = "EmailId cannot be empty")
  @Schema(description = "EmailId of the User creating the schedule.",
      nullable = false)
  private final String email;

  @Valid
  @NotNull(message = "Date cannot be empty")
  @Schema(description = "Date for which the schedule is being created.",
      nullable = false)
  private final LocalDate date;

  @Valid
  @NotNull(message = "availableTimes cannot be empty")
  @Schema(description = "A list of user availability slots(startTime, endTime) in HH:MM:SS format. A slot should be of at least 15 minutes.",
      nullable = false)
  @JsonProperty("availableTimes")
  private final List<AvailableTimes> availableTimes;

  @Valid
  @Schema(description = "Timezone in which the schedule is being created.",
          nullable = true)
  private final String timezone;

}
