package ru.practicum.event.dto;

public interface UpdateEventRequest {
    String getAnnotation();

    Long getCategory();

    String getDescription();

    java.time.LocalDateTime getEventDate();

    ru.practicum.event.model.Location getLocation();

    Boolean getPaid();

    Long getParticipantLimit();

    Boolean getRequestModeration();

    String getTitle();

    void setAnnotation(String annotation);

    void setCategory(Long category);

    void setDescription(String description);

    void setEventDate(java.time.LocalDateTime eventDate);

    void setLocation(ru.practicum.event.model.Location location);

    void setPaid(Boolean paid);

    void setParticipantLimit(Long participantLimit);

    void setRequestModeration(Boolean requestModeration);

    void setTitle(String title);
}
