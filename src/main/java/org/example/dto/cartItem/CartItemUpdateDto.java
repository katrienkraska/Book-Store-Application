package org.example.dto.cartItem;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemUpdateDto {
    @Positive
    private int quantity;
}
