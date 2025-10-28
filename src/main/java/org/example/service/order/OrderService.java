package org.example.service.order;

import org.example.dto.order.OrderRequestDto;
import org.example.dto.order.OrderResponseDto;
import org.example.dto.order.OrderUpdateDto;
import org.example.dto.orderItem.OrderItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto createOrder(Long userId, OrderRequestDto orderDto);

    Page<OrderResponseDto> getUserOrderHistory(Long authentication, Pageable pageable);

    OrderUpdateDto updateOrderStatus(Long authentication, Long orderId,
                                     OrderUpdateDto updateStatus);

    Page<OrderItemResponseDto> getOrderItems(Long authentication, Long orderId,
                                             Pageable pageable);

    OrderItemResponseDto getOrderItem(Long authentication, Long orderId, Long itemId);
}
