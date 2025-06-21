package org.example.service;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Page<BookDto> findAll(Pageable pageable);

    BookDto getBookById(Long id);

    BookDto createBook(CreateBookRequestDto bookDto);

    BookDto update(Long id, CreateBookRequestDto updateBook);

    void delete(Long id);
}
