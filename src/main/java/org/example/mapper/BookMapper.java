package org.example.mapper;

import org.example.dto.book.BookDto;
import org.example.dto.book.CreateBookRequestDto;
import org.example.dto.category.BookDtoWithoutCategoryIds;
import org.example.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    Book toEntity(CreateBookRequestDto bookDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    List<BookDtoWithoutCategoryIds> toDtoWithoutCategories(List<Book> books);

    void updateBookFromDto(
            CreateBookRequestDto dto, @MappingTarget Book book);
}
