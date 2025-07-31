package org.example.mapper;

import org.example.dto.book.BookDto;
import org.example.dto.book.CreateBookRequestDto;
import org.example.model.book.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(
            CreateBookRequestDto dto, @MappingTarget Book book);
}
