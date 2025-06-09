package org.example.service;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import org.example.model.Book;

import java.util.List;

public interface BookService {
    List<BookDto> findAll();

    BookDto getBookById(Long id);

    BookDto createBook(CreateBookRequestDto bookDto);

    Book update(Long id, Book updateBook);

    void delete(Long id);
}
