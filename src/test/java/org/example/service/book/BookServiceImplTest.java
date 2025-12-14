package org.example.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.example.dto.book.BookDto;
import org.example.dto.book.CreateBookRequestDto;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.BookMapper;
import org.example.model.Book;
import org.example.model.Category;
import org.example.repository.BookRepository;
import org.example.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void getBookById_validId_returnsBookDto() {
        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        book.setTitle("Title");

        BookDto expectedDto = new BookDto();
        expectedDto.setId(id);
        expectedDto.setTitle("Title");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actual = bookService.getBookById(id);

        assertEquals(expectedDto, actual);
        verify(bookRepository).findById(id);
        verify(bookMapper).toDto(book);
    }

    @Test
    void getBookById_notFound_throwsException() {
        Long id = 99L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(id));

        verify(bookRepository).findById(id);
    }

    @Test
    void findAll_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Book book1 = new Book();
        book1.setId(1L);

        Book book2 = new Book();
        book2.setId(2L);

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);

        Page<Book> bookPage = new PageImpl<>(List.of(book1, book2));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        Page<BookDto> result = bookService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(bookDto1, result.getContent().get(0));
        assertEquals(bookDto2, result.getContent().get(1));

        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book1);
        verify(bookMapper).toDto(book2);
    }

    @Test
    void createBook_success() {
        Long id = 1L;
        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("New Book");

        Book savedBook = new Book();
        savedBook.setId(id);
        savedBook.setTitle("New Book");

        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle("New Book");

        when(bookMapper.toModel(request)).thenReturn(savedBook);
        when(bookRepository.save(savedBook)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(bookDto);

        BookDto result = bookService.createBook(request);

        assertEquals(bookDto, result);

        verify(bookMapper).toModel(request);
        verify(bookRepository).save(savedBook);
        verify(bookMapper).toDto(savedBook);
    }

    @Test
    void update_validId_updatesBook() {
        Long id = 1L;

        CreateBookRequestDto inputDto = new CreateBookRequestDto();
        inputDto.setTitle("Updated Book");

        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Old Title");

        Book savedBook = new Book();
        savedBook.setId(id);
        savedBook.setTitle("Updated Book");

        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setId(id);
        expectedBookDto.setTitle("Updated Book");

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));

        doAnswer(inv -> {
            existingBook.setTitle(inputDto.getTitle());
            return null;
        }).when(bookMapper).updateBookFromDto(inputDto, existingBook);

        when(bookRepository.save(existingBook)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(expectedBookDto);

        BookDto result = bookService.update(id, inputDto);

        assertEquals(expectedBookDto, result);
        assertEquals("Updated Book", existingBook.getTitle());

        verify(bookRepository).findById(id);
        verify(bookMapper).updateBookFromDto(inputDto, existingBook);
        verify(bookRepository).save(existingBook);
        verify(bookMapper).toDto(savedBook);
    }

    @Test
    void update_notFound_throwsException() {
        Long id = 99L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.update(id, new CreateBookRequestDto()));

        verify(bookRepository).findById(id);
    }

    @Test
    void delete_validId_deletes() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);

        bookService.delete(id);

        verify(bookRepository).existsById(id);
        verify(bookRepository).deleteById(id);
    }

    @Test
    void delete_invalidId_throwsException() {
        Long id = 99L;
        when(bookRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookService.delete(id));

        verify(bookRepository).existsById(id);
    }

    @Test
    void save_withoutCategories_success() {
        Long id = 1L;

        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("Books");
        request.setCategoryIds(List.of());

        Book entity = new Book();
        entity.setTitle("Books");

        Book savedEntity = new Book();
        savedEntity.setId(id);
        savedEntity.setTitle("Books");

        BookDto expectedDto = new BookDto();
        expectedDto.setId(id);
        expectedDto.setTitle("Books");

        when(bookMapper.toModel(request)).thenReturn(entity);
        when(bookRepository.save(entity)).thenReturn(savedEntity);
        when(bookMapper.toDto(savedEntity)).thenReturn(expectedDto);

        BookDto result = bookService.save(request);

        assertEquals(expectedDto, result);

        verify(bookMapper).toModel(request);
        verify(bookRepository).save(entity);
        verify(bookMapper).toDto(savedEntity);
    }

    @Test
    void save_categoryNotFound_throwsException() {
        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setCategoryIds(List.of(100L));

        Book model = new Book();
        when(bookMapper.toModel(request)).thenReturn(model);

        when(categoryRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.save(request));

        verify(categoryRepository).findById(100L);
    }

    @Test
    void save_withCategories_success() {
        Long bookId = 1L;
        Long categoryId = 10L;

        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("Books");
        request.setCategoryIds(List.of(categoryId));

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Tech");

        Book entity = new Book();
        entity.setTitle("Books");

        Book savedEntity = new Book();
        savedEntity.setId(bookId);
        savedEntity.setTitle("Books");
        savedEntity.getCategories().add(category);

        BookDto expectedDto = new BookDto();
        expectedDto.setId(bookId);
        expectedDto.setTitle("Books");

        when(bookMapper.toModel(request)).thenReturn(entity);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(bookRepository.save(entity)).thenReturn(savedEntity);
        when(bookMapper.toDto(savedEntity)).thenReturn(expectedDto);

        BookDto result = bookService.save(request);

        assertEquals(expectedDto, result);

        assertEquals(1, entity.getCategories().size());
        assertEquals(categoryId, entity.getCategories().iterator().next().getId());

        verify(bookMapper).toModel(request);
        verify(categoryRepository).findById(categoryId);
        verify(bookRepository).save(entity);
        verify(bookMapper).toDto(savedEntity);
    }
}
