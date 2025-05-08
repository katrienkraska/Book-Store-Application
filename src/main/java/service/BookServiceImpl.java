package service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import model.Book;
import org.springframework.stereotype.Service;
import repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }
}
