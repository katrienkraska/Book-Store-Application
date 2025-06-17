package org.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookDto {
    private Long id;

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Author must not be blank")
    private String author;

    @NotBlank(message = "ISBN must not be blank")
    private String isbn;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Price must be positive or zero")
    private BigDecimal price;

    @Size(max = 1000, message = "Description can be max 1000 characters")
    private String description;

    private String coverImage;
}
