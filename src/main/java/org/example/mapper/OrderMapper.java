package org.example.mapper;

import org.example.dto.order.OrderResponseDto;
import org.example.model.CartItem;
import org.example.model.Order;
import org.example.model.ShoppingCart;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.example.model.Status;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderDate", dateFormat = "yyyy-MM-dd HH")
    @Mapping(target = "userId", source = "user.id")
    OrderResponseDto toOrderDto(Order order);

    List<OrderResponseDto> toOrderDtoList(List<Order> orders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "total", source = "cart.cartItems", qualifiedByName = "total")
    @Mapping(target = "orderItems", source = "cart.cartItems")
    @Mapping(target = "user", source = "cart.user")
    Order cartToOrder(ShoppingCart cart, String shippingAddress);

    @AfterMapping
    default void updateOrder(@MappingTarget Order order) {
        order.getOrderItems().forEach(oi -> oi.setOrder(order));
        if (order.getStatus() == null) {
            order.setStatus(Status.PENDING);
        }
    }

    @Named("total")
    default BigDecimal getTotal(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(i -> i.getBook().getPrice().multiply(
                        BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
