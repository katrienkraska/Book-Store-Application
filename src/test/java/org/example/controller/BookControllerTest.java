package org.example.controller;

import org.example.dto.book.BookDto;
import org.example.dto.book.CreateBookRequestDto;
import org.example.service.book.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class BookControllerTest {
    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getBookById_returnsBookDto() throws Exception {
        Long id = 1L;
        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle("Title");

        when(bookService.getBookById(id)).thenReturn(bookDto);

        mockMvc.perform(get("/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void findAll_returnsBooks() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");

        when(bookService.findAll(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    return new PageImpl<>(List.of(bookDto), pageable, 1);
                });

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Book"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void createBook_returnsCreatedBook() throws Exception {
        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("New Book");
        request.setAuthor("Author Name");
        request.setIsbn("123-4567890123");
        request.setPrice(BigDecimal.valueOf(100));
        request.setCategoryIds(List.of(1L, 2L));

        BookDto response = new BookDto();
        response.setId(1L);
        response.setTitle("New Book");

        when(bookService.createBook(any(CreateBookRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Book"));
    }

    @Test
    void update_returnsUpdatedBook() throws Exception {
        Long id = 1L;
        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("Updated Book");
        request.setAuthor("Author Name");
        request.setIsbn("123-4567890123");
        request.setPrice(BigDecimal.valueOf(150));
        request.setCategoryIds(List.of(1L, 2L));

        BookDto response = new BookDto();
        response.setId(id);
        response.setTitle("Updated Book");

        when(bookService.update(any(Long.class), any(CreateBookRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/books/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/books/{id}", id))
                .andExpect(status().isNoContent());
    }
}
