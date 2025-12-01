package org.example.controller;

import org.example.dto.category.BookDtoWithoutCategoryIds;
import org.example.dto.category.CategoryDto;
import org.example.service.category.CategoryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest {
    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void createCategory_shouldReturnCreatedCategory() throws Exception {
        Long id = 1L;
        CategoryDto request = new CategoryDto();
        request.setName("New Category");
        request.setDescription("Description");

        CategoryDto response = new CategoryDto();
        response.setId(id);
        response.setName("New Category");
        response.setDescription("Description");

        when(categoryService.save(any(CategoryDto.class))).thenReturn(response);

        mockMvc.perform(post("/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Category"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void getAll() throws Exception {
        Long id = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setName("Category");
        categoryDto.setDescription("Description");

        when(categoryService.findAll(any(Pageable.class)))
                .thenAnswer(invocationOnMock -> {
                    Pageable pageable = invocationOnMock.getArgument(0);
                    return new PageImpl<>(List.of(categoryDto), pageable, 1);
                });

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Category"))
                .andExpect(jsonPath("$.content[0].description").value("Description"));
    }

    @Test
    void getCategoryById() throws Exception {
        Long id = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setName("Category");
        categoryDto.setDescription("Description");

        when(categoryService.getById(id)).thenReturn(categoryDto);

        mockMvc.perform(get("/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Category"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void updateCategory() throws Exception {
        Long id = 1L;
        CategoryDto request = new CategoryDto();
        request.setName("Updated Category");
        request.setDescription("Description");

        CategoryDto response = new CategoryDto();
        response.setId(id);
        response.setName("Updated Category");
        response.setDescription("Description");

        when(categoryService.update(any(Long.class), any(CategoryDto.class))).thenReturn(response);

        mockMvc.perform(put("/categories/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Category"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void deleteCategory() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/categories/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void getBooksByCategoryId() throws Exception {
        Long id = 1L;

        BookDtoWithoutCategoryIds book1 = new BookDtoWithoutCategoryIds();
        book1.setTitle("Book A");
        book1.setAuthor("Author A");
        book1.setIsbn("111-1111111111");
        book1.setPrice(BigDecimal.valueOf(10));
        book1.setDescription("Desc A");
        book1.setCoverImage("imageA.png");

        when(categoryService.getBooksByCategoryId(id))
                .thenReturn(List.of(book1));

        mockMvc.perform(get("/categories/{id}/books", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book A"))
                .andExpect(jsonPath("$[0].author").value("Author A"))
                .andExpect(jsonPath("$[0].isbn").value("111-1111111111"))
                .andExpect(jsonPath("$[0].price").value(10))
                .andExpect(jsonPath("$[0].description").value("Desc A"))
                .andExpect(jsonPath("$[0].coverImage").value("imageA.png"));
    }
}
