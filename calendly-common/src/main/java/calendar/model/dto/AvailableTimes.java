package calendar.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"startTime", "endTime"})
public class AvailableTimes {

  @Schema(description = "Time in HH:MM:SS format", example = "10:20:25")
  @NotNull(message = "Start time must not be null")
  private final LocalTime startTime;

  @Schema(description = "Time in HH:MM:SS format", example = "10:20:25")
  @NotNull(message = "End time must not be null")
  private final LocalTime endTime;

}

