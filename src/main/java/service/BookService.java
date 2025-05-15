package service;

import dao.BookDto;
import dao.CreateBookRequestDto;
import model.Book;
import java.util.List;

public interface BookService {
    List<BookDto> findAll();

    Book save(Book book);

    List getAll();

    BookDto getBookById(Long id);

    BookDto createBook(CreateBookRequestDto bookDto);
}
