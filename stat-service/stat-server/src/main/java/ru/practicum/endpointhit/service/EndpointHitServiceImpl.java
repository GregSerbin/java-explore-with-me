package ru.practicum.endpointhit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.DateFormat;
import ru.practicum.endpointhit.model.EndpointHit;
import ru.practicum.endpointhit.repository.EndpointHitRepository;
import ru.practicum.statsview.model.StatsView;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public void save(EndpointHit endpointHit) {
        endpointHitRepository.save(endpointHit);
        log.info("Запись о статистике была добавлена");
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsView> findByParams(String start, String end, List<String> uris, boolean unique) {
        List<StatsView> listViewStats;

        if (CollectionUtils.isEmpty(uris)) {
            uris = endpointHitRepository.findUniqueUri();
        }

        if (unique) {
            listViewStats = endpointHitRepository.findViewStatsByStartAndEndAndUriAndUniqueIp(decodeTime(start),
                    decodeTime(end),
                    uris);
        } else {
            listViewStats = endpointHitRepository.findViewStatsByStartAndEndAndUri(decodeTime(start),
                    decodeTime(end),
                    uris);
        }

        log.info("Получение статистики просмотров завершено.");
        return listViewStats;
    }

    private LocalDateTime decodeTime(String time) {
        String decodeTime = URLDecoder.decode(time, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decodeTime, DateFormat.FORMATTER);
    }
}