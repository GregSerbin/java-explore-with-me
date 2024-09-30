package ru.practicum.endpointhit.service;

import ru.practicum.endpointhit.model.EndpointHit;
import ru.practicum.statsview.model.StatsView;

import java.util.List;

public interface EndpointHitService {
    void save(EndpointHit endpointHit);

    List<StatsView> findByParams(String start, String end, List<String> uris, boolean unique);
}
