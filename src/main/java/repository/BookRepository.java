package repository;

import dao.BookDto;
import dao.CreateBookRequestDto;
import model.Book;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();

    List getAll();

    Optional<Book> findById(Long id);


    BookDto createBook(CreateBookRequestDto bookDto);
}
