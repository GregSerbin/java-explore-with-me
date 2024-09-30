package ru.practicum.endpointhit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsViewDto;
import ru.practicum.endpointhit.mapper.EndpointHitMapper;
import ru.practicum.endpointhit.service.EndpointHitService;
import ru.practicum.statsview.mapper.StatsViewMapper;
import ru.practicum.statsview.model.StatsView;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EndpointHitController {
    private final EndpointHitService endpointHitService;
    private final EndpointHitMapper endpointHitMapper;
    private final StatsViewMapper viewStatsMapper;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("hit")
    public void save(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Получен запрос на сохранение статистики {}", endpointHitDto);
        endpointHitService.save(endpointHitMapper.endpointHitDtoToEndpointHit(endpointHitDto));
    }

    @GetMapping("stats")
    public List<StatsViewDto> findByParams(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false) boolean unique) {
        log.info("Получен запрос на получение статистики с параметрами start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        List<StatsView> viewStats = endpointHitService.findByParams(start, end, uris, unique);
        return viewStatsMapper.listViewStatsToListViewStatsDto(viewStats);
    }
}
