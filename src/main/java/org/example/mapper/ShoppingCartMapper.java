package org.example.mapper;

import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface ShoppingCartMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems", qualifiedByName = "mapCartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
