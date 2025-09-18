package org.example.service.book;

import org.example.dto.book.BookDto;
import org.example.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookDto> findAll(Pageable pageable);

    BookDto getBookById(Long id);

    BookDto createBook(CreateBookRequestDto bookDto);

    BookDto update(Long id, CreateBookRequestDto updateBook);

    void delete(Long id);

    BookDto save(CreateBookRequestDto requestDto);
}
