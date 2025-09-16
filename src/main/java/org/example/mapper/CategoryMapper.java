package org.example.mapper;

import org.example.dto.category.CategoryDto;
import org.example.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDTO);

    void updateFromDto(CategoryDto dto, @MappingTarget Category category);
}
