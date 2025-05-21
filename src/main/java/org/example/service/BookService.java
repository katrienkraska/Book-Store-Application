package org.example.service;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    List<BookDto> findAll();

    BookDto getBookById(Long id);

    BookDto createBook(CreateBookRequestDto bookDto);
}
