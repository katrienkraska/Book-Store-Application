package org.example.mapper;

import org.example.dto.shoppingcart.ShoppingCartItemDto;
import org.example.model.CartItem;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(source = "book.title", target = "bookTitle")
    ShoppingCartItemDto toItemDto(CartItem cartItem);

    @Named("mapCartItems")
    default Set<ShoppingCartItemDto> mapCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toItemDto)
                .collect(Collectors.toSet());
    }
}
