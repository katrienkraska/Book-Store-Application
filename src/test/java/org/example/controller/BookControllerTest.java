package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.dto.book.BookDto;
import org.example.dto.book.CreateBookRequestDto;
import org.example.model.Book;
import org.example.model.Category;
import org.example.repository.BookRepository;
import org.example.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@Transactional
@Sql(
        scripts = "src/test/resources/database/delete-all-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class BookControllerTest {
    @Container
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;

    @BeforeEach
    void setup() {
        Category category = new Category();
        category.setName("Fantasy");
        category.setDescription("Description");
        savedCategory = categoryRepository.save(category);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBookById_returnsBookDto() throws Exception {
        Book book = new Book();
        book.setTitle("Test");
        book.setAuthor("A");
        book.setIsbn("111");
        book.setPrice(BigDecimal.TEN);
        book.getCategories().add(savedCategory);
        Book savedBook = bookRepository.save(book);

        String json = mockMvc.perform(get("/books/" + savedBook.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto actual = objectMapper.readValue(json, BookDto.class);

        assertThat(actual.getId()).isEqualTo(savedBook.getId());
        assertThat(actual.getTitle()).isEqualTo(savedBook.getTitle());
        assertThat(actual.getAuthor()).isEqualTo(savedBook.getAuthor());
        assertThat(actual.getIsbn()).isEqualTo(savedBook.getIsbn());
        assertThat(actual.getPrice()).isEqualTo(savedBook.getPrice());
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAll_shouldReturnPagedBooks() throws Exception {
        Book book = new Book();
        book.setTitle("X");
        book.setAuthor("A");
        book.setIsbn("222");
        book.setPrice(BigDecimal.ONE);
        book.getCategories().add(savedCategory);
        Book savedBook = bookRepository.save(book);

        String json = mockMvc.perform(get("/books?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<BookDto> books = objectMapper.readValue(
                contentNode.toString(),
                new TypeReference<List<BookDto>>() {
                }
        );

        assertThat(books).hasSize(1);

        BookDto actual = books.get(0);

        assertThat(actual.getId()).isEqualTo(savedBook.getId());
        assertThat(actual.getTitle()).isEqualTo(savedBook.getTitle());
        assertThat(actual.getAuthor()).isEqualTo(savedBook.getAuthor());
        assertThat(actual.getIsbn()).isEqualTo(savedBook.getIsbn());
        assertThat(actual.getPrice()).isEqualTo(savedBook.getPrice());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBook_shouldCreateNewBook() throws Exception {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setTitle("New Book");
        bookRequestDto.setAuthor("A");
        bookRequestDto.setIsbn("ISBN-1");
        bookRequestDto.setPrice(BigDecimal.valueOf(20));
        bookRequestDto.setDescription("Description");
        bookRequestDto.setCategoryIds(List.of(savedCategory.getId()));

        String json = mockMvc.perform(post("/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto actual = objectMapper.readValue(json, BookDto.class);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getTitle()).isEqualTo(bookRequestDto.getTitle());
        assertThat(actual.getAuthor()).isEqualTo(bookRequestDto.getAuthor());
        assertThat(actual.getIsbn()).isEqualTo(bookRequestDto.getIsbn());
        assertThat(actual.getPrice()).isEqualTo(bookRequestDto.getPrice());
        assertThat(actual.getDescription()).isEqualTo(bookRequestDto.getDescription());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_shouldUpdate() throws Exception {
        Book book = new Book();
        book.setTitle("Old");
        book.setAuthor("A");
        book.setIsbn("333");
        book.setPrice(BigDecimal.ONE);
        book.getCategories().add(savedCategory);
        Book savedBook = bookRepository.save(book);

        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setTitle("Updated");
        bookRequestDto.setAuthor("A");
        bookRequestDto.setIsbn("333");
        bookRequestDto.setPrice(BigDecimal.TEN);
        bookRequestDto.setDescription("new description");
        bookRequestDto.setCategoryIds(List.of(savedCategory.getId()));

        String json = mockMvc.perform(put("/books/" + savedBook.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto actual = objectMapper.readValue(json, BookDto.class);

        assertThat(actual.getId()).isEqualTo(savedBook.getId());
        assertThat(actual.getTitle()).isEqualTo(bookRequestDto.getTitle());
        assertThat(actual.getAuthor()).isEqualTo(bookRequestDto.getAuthor());
        assertThat(actual.getIsbn()).isEqualTo(bookRequestDto.getIsbn());
        assertThat(actual.getPrice()).isEqualTo(bookRequestDto.getPrice());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_shouldReturnNoContent() throws Exception {
        Book book = new Book();
        book.setTitle("Del");
        book.setAuthor("A");
        book.setIsbn("444");
        book.setPrice(BigDecimal.ONE);
        book.getCategories().add(savedCategory);
        Book savedBook = bookRepository.save(book);

        mockMvc.perform(delete("/books/" + savedBook.getId()))
                .andExpect(status().isNoContent());

        assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
    }
}
