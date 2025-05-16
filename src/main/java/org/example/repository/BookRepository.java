package org.example.repository;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import org.example.model.Book;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository {
    Book save(Book book);
    List<Book> findAll();
    Optional<Book> findById(Long id);
    BookDto createBook(CreateBookRequestDto bookDto);
}
