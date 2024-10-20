package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.DateFormat;
import ru.practicum.event.enums.StateActionUser;
import ru.practicum.event.model.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest implements UpdateEventRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    @Positive
    Long category;
    @Size(min = 20, max = 7000)
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormat.DATE_TIME_FORMAT)
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero
    Long participantLimit;
    Boolean requestModeration;
    @Size(min = 3, max = 120)
    String title;
    StateActionUser stateAction;
}
