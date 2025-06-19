package org.example.mapper;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import org.example.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
  
    void updateBookFromDto(
            CreateBookRequestDto dto, @MappingTarget Book book);
}
