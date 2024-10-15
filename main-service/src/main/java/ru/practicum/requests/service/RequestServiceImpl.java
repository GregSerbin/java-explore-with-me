package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.errorHandler.IntegrityViolationException;
import ru.practicum.errorHandler.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не существует", userId)));
        List<Request> requests = requestsRepository.findAllByRequesterId(userId);
        return requestMapper.listRequestToListParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        requestsRepository.findByEventIdAndRequesterId(eventId, userId).ifPresent(
                r -> {
                    throw new IntegrityViolationException(String.format("Запрос с пользователем id=%d и событием id=%d не найден", userId, eventId));
                });

        eventRepository.findByIdAndInitiatorId(eventId, userId).ifPresent(
                r -> {
                    throw new IntegrityViolationException("Нарушение целостности");
                });

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не существует", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не существует", eventId)));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IntegrityViolationException(String.format("Событие с id=%d не опубликовано", eventId));
        }

        List<Request> confirmedRequests = requestsRepository.findAllByStatusAndEventId(Status.CONFIRMED, eventId);

        if ((!event.getParticipantLimit().equals(0L))
                && (event.getParticipantLimit() == confirmedRequests.size())) {
            throw new IntegrityViolationException("Превышен лимит запросов");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setEvent(event);

        if ((event.getParticipantLimit().equals(0L)) || (!event.getRequestModeration())) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }

        request = requestsRepository.save(request);
        return requestMapper.requestToParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не существует", userId)));
        Request request = requestsRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Запрос с id=%d не существует", userId)));
        request.setStatus(Status.CANCELED);
        return requestMapper.requestToParticipationRequestDto(request);
    }
}
