package org.example.mapper;

import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.dto.shoppingcart.ShoppingCartItemDto;
import org.example.model.CartItem;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(source = "book.title", target = "bookTitle")
    ShoppingCartItemDto toItemDto(CartItem cartItem);

    @Mapping(target = "userId", source = "cartItem.shoppingCart.user.id")
    @Mapping(target = "cartItems", source = "cartItem.shoppingCart.cartItems")
    ShoppingCartDto toResponseDto(CartItem cartItem);

    @Mapping(source = "user.id", target = "userId")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Named("mapCartItems")
    default Set<ShoppingCartItemDto> mapCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toItemDto)
                .collect(Collectors.toSet());
    }
}
