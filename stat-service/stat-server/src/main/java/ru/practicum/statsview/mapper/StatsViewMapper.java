package ru.practicum.statsview.mapper;

import org.mapstruct.Mapper;
import ru.practicum.StatsViewDto;
import ru.practicum.statsview.model.StatsView;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatsViewMapper {
    List<StatsViewDto> listViewStatsToListViewStatsDto(List<StatsView> viewStats);
}
