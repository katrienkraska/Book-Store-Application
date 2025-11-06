package org.example.mapper;

import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.dto.shoppingcart.ShoppingCartItemDto;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems", qualifiedByName = "mapCartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Named("mapCartItems")
    default Set<ShoppingCartItemDto> mapCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                    ShoppingCartItemDto dto = new ShoppingCartItemDto();
                    dto.setId(cartItem.getId());
                    dto.setBookId(cartItem.getBook().getId());
                    dto.setBookTitle(cartItem.getBook().getTitle());
                    dto.setQuantity(cartItem.getQuantity());
                    return dto;
                })
                .collect(Collectors.toSet());
    }
}
