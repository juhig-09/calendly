package calendar.model.dto;

import calendar.model.enums.MeetingStatus;
import calendar.model.enums.MeetingType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Builder(toBuilder = true)
public class MeetingDto {

  @Valid
  @NotNull(message = "Title cannot be empty.")
  @Schema(description = "Title of the meeting.",
      nullable = false)
  private final String title;

  @Valid
  @Schema(description = "Description of the meeting.",
      nullable = true)
  private final String description;

  @Valid
  @Schema(description = "Timezone in which the meeting is being scheduled. Defaults to the user's timezone who is creating the meeting.",
      nullable = true)
  private final String timezone;

  @Valid
  @NotNull(message = "availableTimes cannot be empty")
  @Schema(description = "A tuple mentioning start and end time of the meeting.",
      nullable = false)
  private final AvailableTimes meetingTime;

  @Valid
  @NotNull(message = "Date cannot be empty")
  @Schema(description = "Date of the meeting.",
      nullable = false)
  private final LocalDate date;

  @Valid
  @NotNull
  @Schema(description = "Type of the meeting - ZOOM, GOOGLE_MEET, TEAMS",
      nullable = false)
  private final MeetingType meetingType;

  @Valid
  @NotNull(message = "Meeting link cannot be empty")
  @Schema(description = "Meeting link.",
      nullable = false)
  private final String link;

  @Valid
  @Schema(description = "Required people in the meeting. If required member is not available, meeting will not be scheduled.",
      nullable = true)
  private List<String> requiredInvitees;

  @Valid
  @Schema(description = "Optional people in the meeting. Availability of these people will not affect meeting status.",
      nullable = true)
  private List<String> optionalInvitees;

  @Valid
  @Schema(description = "Status of the Meeting",
      nullable = false)
  private final MeetingStatus status;

}