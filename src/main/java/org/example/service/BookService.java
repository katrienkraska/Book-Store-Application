package org.example.service;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import org.example.model.Book;
import java.util.List;

public interface BookService {
    List<BookDto> findAll();
    Book save(Book book);
    BookDto getBookById(Long id);
    BookDto createBook(CreateBookRequestDto bookDto);
}
