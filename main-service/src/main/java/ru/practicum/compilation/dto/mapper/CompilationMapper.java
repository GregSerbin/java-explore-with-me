package ru.practicum.compilation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "events", ignore = true)
    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto);

    CompilationDto compilationToCompilationDto(Compilation compilation);

    List<CompilationDto> listCompilationToListCompilationDto(List<Compilation> compilations);
}
