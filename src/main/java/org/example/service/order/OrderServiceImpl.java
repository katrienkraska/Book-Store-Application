package org.example.service.order;

import org.example.dto.order.OrderRequestDto;
import org.example.dto.order.OrderResponseDto;
import org.example.dto.order.OrderUpdateDto;
import org.example.dto.orderItem.OrderItemResponseDto;
import org.example.model.Order;
import org.example.model.ShoppingCart;
import org.example.exception.EntityNotFoundException;
import org.example.exception.OrderProcessingException;
import org.example.mapper.OrderItemMapper;
import org.example.mapper.OrderMapper;
import org.example.model.Status;
import org.example.repository.OrderRepository;
import org.example.repository.OrderItemRepository;
import org.example.repository.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderResponseDto createOrder(Long userId, OrderRequestDto orderDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user id: " + userId));

        if (shoppingCart.getCartItems().isEmpty()) {
            throw new OrderProcessingException(
                    "The cart is empty — can't create order for user " + userId);
        }

        Order order = orderMapper.cartToOrder(shoppingCart, orderDto.getShippingAddress());
        shoppingCart.clearCart();
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponseDto> getUserOrderHistory(Long userId, Pageable pageable) {
        return orderRepository.getAllByUserId(userId, pageable)
                .map(orderMapper::toOrderDto);
    }

    @Override
    public OrderUpdateDto updateOrderStatus(Long userId, Long orderId, OrderUpdateDto updateStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find order by id " + orderId));

        String statusStr = updateStatus.getStatus().trim().toUpperCase();

        try {
            Status status = Status.valueOf(statusStr);
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new OrderProcessingException(
                    "Invalid status: '" + updateStatus.getStatus() +
                            "'. Allowed values: " + java.util.Arrays.toString(Status.values()));
        }

        orderRepository.save(order);
        return updateStatus;
    }

    @Override
    public Page<OrderItemResponseDto> getOrderItems(Long userId, Long orderId, Pageable pageable) {
        return orderItemRepository.findByOrderIdAndUserId(orderId, userId, pageable)
                .map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemResponseDto getOrderItem(Long userId, Long orderId, Long itemId) {
        return orderItemRepository
                .findByIdAndOrderIdAndUserId(itemId, orderId, userId)
                .map(orderItemMapper::toDto).orElseThrow(
                        () -> new EntityNotFoundException("Order item not found "));
    }
}
