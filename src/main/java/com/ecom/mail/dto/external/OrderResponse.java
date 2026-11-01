package com.ecom.mail.dto.external;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long bookingId,
    Long addressId,
    Long userId,
    String username,
    String totalAmount,
    OrderStatus status,
    LocalDateTime createdAt,
    List<OrderItemResponse> items,
    UserDto user,
    AddressDto address
) {
    public OrderResponse(
        Long bookingId,
        Long addressId,
        Long userId,
        String username,
        String totalAmount,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
    ) {
        this(bookingId, addressId, userId, username, totalAmount, status, createdAt, items, null, null);
    }
}