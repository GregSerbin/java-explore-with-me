package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.errorHandler.IntegrityViolationException;
import ru.practicum.errorHandler.NotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Category addCategory(Category category) {
        log.info("Начало процесса создания категории");
        categoryRepository.findCategoriesByNameContainingIgnoreCase(category.getName().toLowerCase()).ifPresent(c -> {
            throw new IntegrityViolationException(String.format("Категория с именем %s уже существует", category.getName()));
        });
        Category createCategory = categoryRepository.save(category);
        log.info("Категория была создана");
        return createCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        log.info("Начало процесса удаления категории");
        categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException(String.format("Категория с id=%d не существует", catId)));
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new IntegrityViolationException(String.format("Категория с id=%s уже существует", catId));
        }
        categoryRepository.deleteById(catId);
        log.info("Категория была удалена");
    }

    @Override
    @Transactional
    public Category updateCategory(long catId, Category newCategory) {
        log.info("Начало процесса обновления категории");
        Category updateCategory = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException(String.format("Категория с id=%d не существует", catId)));
        categoryRepository.findCategoriesByNameContainingIgnoreCase(
                newCategory.getName().toLowerCase()).ifPresent(c -> {
            if (c.getId() != catId) {
                throw new IntegrityViolationException(String.format("Категория с именем %s уже существует", newCategory.getName()));
            }
        });
        updateCategory.setName(newCategory.getName());
        log.info("Категория была обновлена");
        return updateCategory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories(int from, int size) {
        log.info("Начало процесса поиска всех категорий");
        PageRequest pageRequest = PageRequest.of(from, size);
        Page<Category> pageCategories = categoryRepository.findAll(pageRequest);
        List<Category> categories;

        if (pageCategories.hasContent()) {
            categories = pageCategories.getContent();
        } else {
            categories = Collections.emptyList();
        }

        log.info("Поиск категорий закончен");
        return categories;
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategory(long catId) {
        log.info("Начало процесса поиска категории по id");
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with id=" + catId + " does not exist"));
        log.info("Поиск категории закончен");
        return category;
    }
}
