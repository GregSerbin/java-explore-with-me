package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.dto.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.errorHandler.NotFoundException;
import ru.practicum.event.repository.EventRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        log.info("Начало процесса создания подборки");
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(compilationDto);
        List<Long> ids = compilationDto.getEvents();

        if (!CollectionUtils.isEmpty(ids)) {
            compilation.setEvents(eventRepository.findAllByIdIn(ids));
        } else {
            compilation.setEvents(Collections.emptyList());
        }

        Compilation createdCompilation = compilationRepository.save(compilation);
        log.info("Подборка была создана");
        return compilationMapper.compilationToCompilationDto(createdCompilation);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest request) {
        log.info("Начало процесса обновления подборки");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Подборка с id=%d не существует", compId)));

        if (!CollectionUtils.isEmpty(request.getEvents())) {
            compilation.setEvents(eventRepository.findAllByIdIn(request.getEvents()));
        }

        if (request.getPinned() != null) compilation.setPinned(request.getPinned());

        if (request.getTitle() != null) compilation.setTitle(request.getTitle());

        log.info("Подборка была обновлена");
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(long compId) {
        log.info("Начало процесса удаления подборки");
        compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Подборка с id=%d не существует", compId)));
        compilationRepository.deleteById(compId);
        log.info("Подборка была удалена");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        log.info("Начало процесса поиска всех подборок");
        PageRequest pageRequest = PageRequest.of(from, size);
        List<CompilationDto> compilationsDto;

        if (pinned == null) {
            compilationsDto = compilationMapper.listCompilationToListCompilationDto(compilationRepository
                    .findAll(pageRequest).getContent());
        } else if (pinned) {
            compilationsDto = compilationMapper.listCompilationToListCompilationDto(
                    compilationRepository.findAllByPinnedTrue(pageRequest).getContent());
        } else {
            compilationsDto = compilationMapper.listCompilationToListCompilationDto(
                    compilationRepository.findAllByPinnedFalse(pageRequest).getContent());
        }

        log.info("Поиск подборок закончен");
        return compilationsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long compId) {
        log.info("Начало процесса поиска подборки по id");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Подборка с id=%d не существует", compId)));
        log.info("Поиск подборки закончен");
        return compilationMapper.compilationToCompilationDto(compilation);
    }
}
