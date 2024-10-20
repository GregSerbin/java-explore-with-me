package ru.practicum.category.dto.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

    CategoryDto categoryToCategoryDto(Category category);

    Category updateCategoryDtoToCategory(UpdateCategoryDto updateCategoryDto);

    List<CategoryDto> listCategoryToListCategoryDto(List<Category> categoryList);
}
