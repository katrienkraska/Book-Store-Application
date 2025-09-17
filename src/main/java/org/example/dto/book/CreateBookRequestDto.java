package org.example.dto.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.dto.category.CategoryRequestDto;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateBookRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISNB is required")
    private String isbn;

    @NotNull(message = "Prise is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Price must be positive or zero")
    private BigDecimal price;

    @Size(max = 1000, message = "Description can be max 1000 characters")
    private String description;

    private String coverImage;

    private List<Long> categoryIds;

    private List<CategoryRequestDto> categories;
}
