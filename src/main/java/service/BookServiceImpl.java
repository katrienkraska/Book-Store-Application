package service;

import java.util.List;
import java.util.stream.Collectors;
import dao.BookDto;
import dao.CreateBookRequestDto;
import exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.BookMapper;
import model.Book;
import org.springframework.stereotype.Service;
import repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List getAll() {
        return bookRepository.getAll();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto createBook(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toModel(bookDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }
}
