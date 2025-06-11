package org.example.service;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import org.example.dto.UpdateBookRequestDto;
import java.util.List;

public interface BookService {
    List<BookDto> findAll();

    BookDto getBookById(Long id);

    BookDto createBook(CreateBookRequestDto bookDto);

    BookDto update(Long id, UpdateBookRequestDto updateBook);

    void delete(Long id);
}
