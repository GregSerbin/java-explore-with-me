package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatClient {

    private final RestClient restClient;

    public StatClient(@Value("${stat-server.url}") String serverUrl) {
        this.restClient = RestClient.create(serverUrl);
        log.info("URL запуска сервера статистики: {}", serverUrl);
    }

    public void saveHit(String app, HttpServletRequest request) {
        log.info("Сохранение вызова для приложения: {}", app);
        EndpointHitDto endpointHitDto = toDto(app, request);
        ResponseEntity<Void> response = restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto)
                .retrieve()
                .toBodilessEntity();
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Отправлен вызов url с кодом: {}", response.getStatusCode());
        } else {
            log.error("Отправлен вызов url с кодом ошибки: {}", response.getStatusCode());
        }
    }

    public ResponseEntity<List<StatsViewDto>> getStats(LocalDateTime start, LocalDateTime end,
                                                       List<String> uris, boolean unique) {
        log.info("Получение статистики для urls: {}", uris);
        try {
            return restClient.get()
                    .uri(uriBuilder ->
                            uriBuilder.path("/stats")
                                    .queryParam("start", start.format(DateFormat.FORMATTER))
                                    .queryParam("end", end.format(DateFormat.FORMATTER))
                                    .queryParam("uris", uris)
                                    .queryParam("unique", unique)
                                    .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            (request, response) ->
                                    log.error("Получение статистики для {} с кодом ошибки {}", uris, response.getStatusCode()))
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Получение статистики для {} завершилось ошибкой.", uris, e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private EndpointHitDto toDto(String app, HttpServletRequest request) {
        return EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}