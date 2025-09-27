package org.example.controller;

import org.example.dto.category.BookDtoWithoutCategoryIds;
import org.example.dto.category.CategoryDto;
import org.example.service.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category", description = "Provides endpoints for managing book categories")
@RequiredArgsConstructor
@RequestMapping("/categories")
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(
            summary = "Create a new category",
            description = "Allows users with 'ADMIN' authority "
                    + "to create a new category in the system. "
                    + "The request must contain valid category data."
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @Operation(
            summary = "Retrieve paginated categories",
            description = "Fetches a paginated list of categories with optional "
                    + "sorting and page size customization."
                    + " By default, categories are sorted in ascending order."
                    + " The sorting criteria should be specified in the format: "
                    + "sort: field,(ASC||DESC)'"
                    + " Only users with the 'USER' authority have access to this endpoint.."
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @Operation(summary = "Get category by ID",
            description = "Allows users with 'USER' authority to retrieve a category by its ID."
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "Update category by ID",
            description = "Allows users with 'ADMIN' authority to update the details of a category."
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.update(id, categoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category by ID",
            description = "Allows users with 'ADMIN' authority to delete a category by its ID."
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @Operation(summary = "Get books by category ID",
            description = "Allows users with 'USER' authority to retrieve a list of books "
                    + "that belong to the specified category."
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{id}/books")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(@PathVariable Long id) {
        return categoryService.getBooksByCategoryId(id);
    }
}
