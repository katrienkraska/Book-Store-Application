package org.example.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.example.dto.category.BookDtoWithoutCategoryIds;
import org.example.dto.category.CategoryDto;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.BookMapper;
import org.example.mapper.CategoryMapper;
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
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void findAll_ShouldReturnPagedCategoryDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category1 = new Category();
        category1.setId(1L);

        Category category2 = new Category();
        category2.setId(2L);

        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(1L);

        CategoryDto categoryDto2 = new CategoryDto();
        category2.setId(2L);

        Page<Category> categoriesPage = new PageImpl<>(List.of(category1, category2));

        when(categoryRepository.findAll(pageable)).thenReturn(categoriesPage);
        when(categoryMapper.toDto(category1)).thenReturn(categoryDto1);
        when(categoryMapper.toDto(category2)).thenReturn(categoryDto2);

        Page<CategoryDto> result = categoryService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(categoryDto1, result.getContent().get(0));
        assertEquals(categoryDto2, result.getContent().get(1));

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category1);
        verify(categoryMapper).toDto(category2);
    }

    @Test
    void getById_ShouldReturnCategoryDto_WhenCategoryExists() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getById(id);

        assertEquals(categoryDto, result);

        verify(categoryRepository).findById(id);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void getById_ShouldThrowException_WhenCategoryNotFound() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(id)
        );

        assertEquals("Can't find category by id 1", exception.getMessage());
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    void save_ShouldReturnSavedCategoryDto() {
        Long id = 1L;
        CategoryDto inputDto = new CategoryDto();
        inputDto.setName("Books");

        Category entity = new Category();
        entity.setName("Books");

        Category savedEntity = new Category();
        savedEntity.setId(id);
        savedEntity.setName("Books");

        CategoryDto expectedDto = new CategoryDto();
        expectedDto.setId(id);
        expectedDto.setName("Books");

        when(categoryMapper.toEntity(inputDto)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(savedEntity);
        doReturn(expectedDto).when(categoryMapper).toDto(any(Category.class));

        CategoryDto result = categoryService.save(inputDto);

        assertEquals(expectedDto, result);

        verify(categoryMapper).toEntity(inputDto);
        verify(categoryRepository).save(entity);
        verify(categoryMapper).toDto(any(Category.class));
    }

    @Test
    void update_ShouldReturnUpdatedCategoryDto_WhenCategoryExists() {
        Long id = 1L;
        CategoryDto inputDto = new CategoryDto();
        inputDto.setName("updated");

        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("old name");

        Category savedCategory = new Category();
        savedCategory.setId(id);
        savedCategory.setName("updated");

        CategoryDto expectedDto = new CategoryDto();
        expectedDto.setId(id);
        expectedDto.setName("updated");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        doNothing().when(categoryMapper).updateFromDto(inputDto, existingCategory);
        when(categoryRepository.save(existingCategory)).thenReturn(savedCategory);
        doReturn(expectedDto).when(categoryMapper).toDto(any(Category.class));

        CategoryDto result = categoryService.update(id, inputDto);

        assertEquals(expectedDto, result);

        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).updateFromDto(inputDto, existingCategory);
        verify(categoryMapper).toDto(any(Category.class));
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(id, new CategoryDto()));
    }

    @Test
    void deleteById_ShouldDelete_WhenExists() {
        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.deleteById(id);

        verify(categoryRepository).deleteById(id);
    }

    @Test
    void deleteById_ShouldThrow_WhenNotFound() {
        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.deleteById(id)
        );

        assertEquals("Can't find category by id 1", exception.getMessage());

        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    void getBooksByCategoryId_ShouldReturnMappedDtos() {
        Long id = 10L;
        Book book = new Book();
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();

        when(bookRepository.findByCategoriesId(id)).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(dto);

        List<BookDtoWithoutCategoryIds> result = categoryService.getBooksByCategoryId(id);

        assertEquals(1, result.size());
        verify(bookRepository).findByCategoriesId(id);
        verify(bookMapper).toDtoWithoutCategories(book);
    }
}
