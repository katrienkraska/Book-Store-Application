package org.example.dto.shoppingcart;

import lombok.Data;

@Data
public class ShoppingCartItemDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}
