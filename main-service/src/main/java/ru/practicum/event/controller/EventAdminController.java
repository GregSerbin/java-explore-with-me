package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DateFormat;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllAdminEvents(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) State state,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = DateFormat.DATE_TIME_FORMAT)
                                                LocalDateTime rangeStart,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = DateFormat.DATE_TIME_FORMAT)
                                                LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size,
                                                @RequestParam(defaultValue = "true") Boolean sortRating) {
        log.info("Получен запрос на получение всех событий с параметрами: users={}, state={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}, sortRating={}", users, state, categories, rangeStart, rangeEnd, from, size, sortRating);
        return eventService.getAllAdminEvents(users, state, categories, rangeStart, rangeEnd, from, size, sortRating);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
                                         @PathVariable long eventId) {
        log.info("Получен запрос на обновление события с id={}", eventId);
        return eventService.updateEventAdmin(updateEventAdminRequest, eventId);
    }
}
