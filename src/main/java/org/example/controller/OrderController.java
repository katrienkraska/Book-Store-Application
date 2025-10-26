package org.example.controller;

import org.example.dto.order.OrderRequestDto;
import org.example.dto.order.OrderResponseDto;
import org.example.dto.order.OrderUpdateDto;
import org.example.dto.orderItem.OrderItemResponseDto;
import org.example.model.User;
import org.example.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "Endpoints for managing customer orders and purchases")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Place an order",
            description = "Allows a user to place an order for books in their shopping cart. "
                    + "The user is identified through their authentication details. "
                    + "Only users with 'USER' authority can perform this action. "
                    + "Upon successful order placement, an order response is returned "
                    + "with the details of the order."
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public OrderResponseDto createOrder(Authentication authentication,
                                        @Valid @RequestBody OrderRequestDto orderRequestDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(user.getId(), orderRequestDto);
    }

    @Operation(
            summary = "Get order history",
            description = "Retrieves a paginated list of past orders for the authenticated user. "
                    + "Only users with 'USER' authority can access this endpoint. "
                    + "The order history is paginated and can be sorted based "
                    + "on the request parameters."
                    + "The sorting criteria should be specified in the format: "
                    + "'sort: field,(ASC||DESC)'"
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public List<OrderResponseDto> getUserOrderHistory(Authentication authentication,
                                                      Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getUserOrderHistory(user.getId(), pageable);
    }

    @Operation(
            summary = "Update order status",
            description = "Allows an admin to update the status of an order. "
                    + "Only users with 'ADMIN' authority can perform this action."
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("{id}")
    public OrderUpdateDto updateOrderStatus(Authentication authentication, @PathVariable Long id,
                                            @Valid @RequestBody OrderUpdateDto updateDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.updateOrderStatus(user.getId(), id, updateDto);
    }

    @Operation(
            summary = "Get all order items by order ID",
            description = "Retrieves a paginated list of items for a specific order. "
                    + "Only users with 'USER' authority can access this."
                    + "The sorting criteria should be specified in the format: "
                    + "'sort: field,(ASC||DESC)'"
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("{orderId}/items")
    public Page<OrderItemResponseDto> getOrderItems(Authentication authentication,
                                                    @PathVariable Long orderId,
                                                    Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItems(user.getId(), orderId, pageable);
    }

    @Operation(
            summary = "Get a specific order item by ID",
            description = "Retrieves details of a specific item within an order. "
                    + "Only users with 'USER' authority can access this."
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{orderId}/items/{id}")
    public OrderItemResponseDto getOrderItem(Authentication authentication,
                                             @PathVariable Long orderId,
                                             @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItem(user.getId(), orderId, id);
    }
}
