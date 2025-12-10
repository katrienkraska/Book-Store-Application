package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.example.dto.category.CategoryDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
@AutoConfigureMockMvc
class CategoryControllerTest {
    @Container
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
        r.add("spring.datasource.username", mysql::getUsername);
        r.add("spring.datasource.password", mysql::getPassword);

        r.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.MySQLDialect");

        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    EntityManager em;

    @BeforeEach
    void clean() {
        em.createNativeQuery("DELETE FROM books_categories").executeUpdate();
        em.createNativeQuery("DELETE FROM books").executeUpdate();
        em.createNativeQuery("DELETE FROM categories").executeUpdate();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_shouldCreate() throws Exception {
        CategoryDto dto = new CategoryDto();
        dto.setName("Tech");
        dto.setDescription("desc");

        mockMvc.perform(post("/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tech"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_shouldReturnCategories() throws Exception {
        Category c = new Category();
        c.setName("Category1");
        c.setDescription("Desc1");
        categoryRepo.save(c);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Category1"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCategoryById_shouldReturnCategory() throws Exception {
        Category c = new Category();
        c.setName("History");
        c.setDescription("d");
        categoryRepo.save(c);

        mockMvc.perform(get("/categories/" + c.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("History"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory() throws Exception {
        Category c = new Category();
        c.setName("Old");
        c.setDescription("D");
        categoryRepo.save(c);

        CategoryDto dto = new CategoryDto();
        dto.setName("Updated Category");
        dto.setDescription("New D");

        mockMvc.perform(put("/categories/" + c.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory() throws Exception {
        Category c = new Category();
        c.setName("Del");
        c.setDescription("D");
        categoryRepo.save(c);

        mockMvc.perform(delete("/categories/" + c.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBooksByCategory_shouldReturnBooks() throws Exception {
        Category category = new Category();
        category.setName("Science");
        category = categoryRepo.save(category);

        Book book = new Book();
        book.setTitle("Quantum Physics");
        book.setAuthor("X");
        book.setIsbn("111");
        book.setPrice(BigDecimal.TEN);
        book.getCategories().add(category);
        bookRepo.save(book);

        mockMvc.perform(get("/categories/" + category.getId() + "/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Quantum Physics"));
    }
}
