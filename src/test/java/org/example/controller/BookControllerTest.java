package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@Transactional
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

    @PersistenceContext
    private EntityManager entityManager;

    private Category savedCategory;

    @BeforeEach
    void setup() {
        clearDb();

        Category category = new Category();
        category.setName("Fantasy");
        category.setDescription("desc");
        savedCategory = categoryRepository.save(category);
    }

    private void clearDb() {
        entityManager.createNativeQuery("DELETE FROM books_categories").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM order_items").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM cart_items").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM shopping_carts").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM books").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM categories").executeUpdate();
        entityManager.flush();
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
        bookRepository.save(book);

        mockMvc.perform(get("/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.author").value("A"));
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
        bookRepository.save(book);

        mockMvc.perform(get("/books?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("X"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBook_shouldCreateNewBook() throws Exception {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("New Book");
        dto.setAuthor("A");
        dto.setIsbn("ISBN-1");
        dto.setPrice(BigDecimal.valueOf(20));
        dto.setDescription("desc");
        dto.setCategoryIds(List.of(savedCategory.getId()));

        mockMvc.perform(post("/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"));
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
        bookRepository.save(book);

        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("Updated");
        dto.setAuthor("A");
        dto.setIsbn("333");
        dto.setPrice(BigDecimal.TEN);
        dto.setCategoryIds(List.of(savedCategory.getId()));

        mockMvc.perform(put("/books/" + book.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
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
        bookRepository.save(book);

        mockMvc.perform(delete("/books/" + book.getId()))
                .andExpect(status().isNoContent());
    }
}
