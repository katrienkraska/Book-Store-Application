package org.example.service.shoppingcart;

import org.example.dto.cartItem.CartItemRequestDto;
import org.example.dto.cartItem.CartItemUpdateDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.model.User;

public interface ShoppingCartService {

    ShoppingCartDto getByUserId(Long authenticationId);

    ShoppingCartDto save(Long authenticationId, CartItemRequestDto requestDto);

    void saveShoppingCartForUser(User user);

    ShoppingCartDto update(Long authenticationId,
                           Long cartItemId, CartItemUpdateDto updateDto);

    void deleteById(Long authenticationId, Long id);
}
