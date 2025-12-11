package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.dto.category.BookDtoWithoutCategoryIds;
import org.example.dto.category.CategoryDto;
import org.example.model.Book;
import org.example.model.Category;
import org.example.repository.BookRepository;
import org.example.repository.CategoryRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@Sql("classpath:database/delete-all-data.sql")
class CategoryControllerTest {
    @Container
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.MySQLDialect");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_shouldCreate() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Tech");
        categoryDto.setDescription("Description");

        String json = mockMvc.perform(post("/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoryDto actual = objectMapper.readValue(json, CategoryDto.class);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(categoryDto.getName());
        assertThat(actual.getDescription()).isEqualTo(categoryDto.getDescription());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_shouldReturnCategories() throws Exception {
        Category category = new Category();
        category.setName("Category1");
        category.setDescription("Description1");
        Category savedCategory = categoryRepository.save(category);

        String json = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<CategoryDto> categories = objectMapper.readValue(
                contentNode.toString(),
                new TypeReference<List<CategoryDto>>() {
                }
        );

        assertThat(categories).hasSize(1);

        CategoryDto actual = categories.get(0);
        assertThat(actual.getId()).isEqualTo(savedCategory.getId());
        assertThat(actual.getName()).isEqualTo(savedCategory.getName());
        assertThat(actual.getDescription()).isEqualTo(savedCategory.getDescription());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCategoryById_shouldReturnCategory() throws Exception {
        Category category = new Category();
        category.setName("History");
        category.setDescription("Description");
        Category savedCategory = categoryRepository.save(category);

        String json = mockMvc.perform(get("/categories/" + savedCategory.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoryDto actual = objectMapper.readValue(json, CategoryDto.class);

        assertThat(actual.getId()).isEqualTo(savedCategory.getId());
        assertThat(actual.getName()).isEqualTo(savedCategory.getName());
        assertThat(actual.getDescription()).isEqualTo(savedCategory.getDescription());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory() throws Exception {
        Category category = new Category();
        category.setName("Old");
        category.setDescription("Description");
        Category savedCategory = categoryRepository.save(category);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Updated Category");
        categoryDto.setDescription("New Description");

        String json = mockMvc.perform(put("/categories/" + savedCategory.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoryDto actual = objectMapper.readValue(json, CategoryDto.class);

        assertThat(actual.getId()).isEqualTo(savedCategory.getId());
        assertThat(actual.getName()).isEqualTo(categoryDto.getName());
        assertThat(actual.getDescription()).isEqualTo(categoryDto.getDescription());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory() throws Exception {
        Category category = new Category();
        category.setName("Delete");
        category.setDescription("Description");
        categoryRepository.save(category);

        Category savedCategory = categoryRepository.save(category);

        mockMvc.perform(delete("/categories/" + savedCategory.getId()))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(savedCategory.getId())).isEmpty();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBooksByCategory_shouldReturnBooks() throws Exception {
        Category category = new Category();
        category.setName("Science");
        Category savedCategory = categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Quantum Physics");
        book.setAuthor("X");
        book.setIsbn("111");
        book.setPrice(BigDecimal.TEN);
        book.getCategories().add(savedCategory);
        Book savedBook = bookRepository.save(book);

        String json = mockMvc.perform(get("/categories/" + savedCategory.getId() + "/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BookDtoWithoutCategoryIds> books = objectMapper.readValue(
                json,
                new TypeReference<List<BookDtoWithoutCategoryIds>>() {}
        );

        assertThat(books).hasSize(1);

        BookDtoWithoutCategoryIds actual = books.get(0);
        assertThat(actual.getTitle()).isEqualTo(savedBook.getTitle());
        assertThat(actual.getAuthor()).isEqualTo(savedBook.getAuthor());
        assertThat(actual.getIsbn()).isEqualTo(savedBook.getIsbn());
        assertThat(actual.getPrice()).isEqualTo(savedBook.getPrice());
    }
}
