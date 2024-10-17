package ru.practicum.like.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.like.model.StatusLike;
import ru.practicum.like.service.LikeService;

@Slf4j
@RestController
@RequestMapping("/event/{eventId}/like/{userId}")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addLike(@PathVariable @Positive long eventId,
                                @PathVariable @Positive long userId,
                                @RequestParam StatusLike reaction) {
        log.info("Получен запрос на добавление лайка событию с id={} пользователем с id={} и реакцией {}", eventId, userId, reaction);
        return likeService.addLike(eventId, userId, reaction);
    }

    @PatchMapping
    public EventFullDto updateLike(@PathVariable @Positive long eventId,
                                   @PathVariable @Positive long userId,
                                   @RequestParam StatusLike reaction) {
        log.info("Получен запрос на обновление лайка событию с id={} пользователем с id={} и реакцией {}", eventId, userId, reaction);
        return likeService.updateLike(eventId, userId, reaction);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable @Positive long eventId,
                           @PathVariable @Positive long userId) {
        log.info("Получен запрос на удаление лайка событию с id={} пользователем с id={}", eventId, userId);
        likeService.deleteLike(eventId, userId);
    }
}
