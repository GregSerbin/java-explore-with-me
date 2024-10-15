package ru.practicum.errorHandler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.DateFormat;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiError {
    String status;
    String reason;
    String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormat.DATE_TIME_FORMAT)
    LocalDateTime timestamp;
    String errors;
}
