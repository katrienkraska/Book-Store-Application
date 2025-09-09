package org.example.dto.category;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookDtoWithoutCategoryIds {
    private String title;

    private String author;

    private String isbn;

    private BigDecimal price;

    private String description;

    private String coverImage;
}
