package ru.practicum.like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.like.model.Like;
import ru.practicum.like.model.StatusLike;
import ru.practicum.like.repository.LikeRepository;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.errorHandler.NotFoundException;
import ru.practicum.errorHandler.RestrictionsViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private static final int DIFFERENCE_RATING_BY_ADD = 1;
    private static final int DIFFERENCE_RATING_BY_DELETE = -1;
    private static final int DIFFERENCE_RATING_BY_UPDATE = 2;

    private final EventRepository eventRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RequestsRepository requestsRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventFullDto addLike(long eventId, long userId, StatusLike statusLike) {
        log.info("Начало процесса добавления лайка событию");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не существует", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не существует", eventId)));

        if (!requestsRepository.existsByEventAndRequesterAndStatus(event, user, Status.CONFIRMED)) {
            throw new RestrictionsViolationException("Только участники события могут ставить лайки");
        }

        if (likeRepository.existsByEventAndUser(event, user)) {
            throw new RestrictionsViolationException("Данное событие уже было оценено");
        }

        if (event.getInitiator().getId() == userId) {
            throw new RestrictionsViolationException("Инициатор события не может оценивать сам себя");
        }

        Like like = new Like();
        like.setUser(user);
        like.setEvent(event);
        like.setStatus(statusLike);
        like.setCreated(LocalDateTime.now());
        likeRepository.save(like);
        changeRatingUserAndEvent(event, statusLike, DIFFERENCE_RATING_BY_ADD);

        log.info("Лай был добавлен {}", statusLike);
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateLike(long eventId, long userId, StatusLike statusLike) {
        log.info("Начало процесса обновления лайка событию");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не существует", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не существует", eventId)));

        Optional<Like> likeOptional = likeRepository.findByEventAndUser(event, user);

        if (likeOptional.isPresent()) {
            Like like = likeOptional.get();
            if (like.getStatus() == statusLike) {
                throw new RestrictionsViolationException("Вы уже оценили событие");
            }
            like.setStatus(statusLike);
            like.setCreated(LocalDateTime.now());
            changeRatingUserAndEvent(event, statusLike, DIFFERENCE_RATING_BY_UPDATE);
        } else {
            throw new NotFoundException("Вы еще не оценивали событие");
        }

        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    @Transactional
    public void deleteLike(long eventId, long userId) {
        log.info("Начало процесса удаления лайка событию");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не существует", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не существует", eventId)));

        Like like = likeRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new RestrictionsViolationException("Вы еще не оценивали событие"));

        StatusLike statusLike = like.getStatus();
        likeRepository.delete(like);
        changeRatingUserAndEvent(event, statusLike, DIFFERENCE_RATING_BY_DELETE);

//        Optional<Like> likeOptional = likeRepository.findByEventAndUser(event, user);

//        if (likeOptional.isPresent()) {
//            Like like = likeOptional.get();
//            StatusLike statusLike = like.getStatus();
//            likeRepository.delete(like);
//            changeRatingUserAndEvent(event, statusLike, DIFFERENCE_RATING_BY_DELETE);
//        } else {
//            throw new RestrictionsViolationException("Вы еще не оценивали событие");
//        }
        log.info("Реакция была удалена");
    }

    private void changeRatingUserAndEvent(Event event, StatusLike statusLike, int difference) {
        User initiatorEvent = userRepository.findById(event.getInitiator().getId())
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не существует", event.getInitiator().getId())));
        if (statusLike == StatusLike.LIKE) {
            initiatorEvent.setRating(initiatorEvent.getRating() + difference);
            event.setRating(event.getRating() + difference);
        } else if (statusLike == StatusLike.DISLIKE) {
            initiatorEvent.setRating(initiatorEvent.getRating() - difference);
            event.setRating(event.getRating() - difference);
        }
    }
}
