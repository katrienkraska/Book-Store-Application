package org.example.dto.shoppingcart;

import lombok.Data;
import java.util.Set;

@Data
public class ShoppingCartDto {
    private Long id;

    private Long userId;

    private Set<ShoppingCartItemDto> cartItems;
}
