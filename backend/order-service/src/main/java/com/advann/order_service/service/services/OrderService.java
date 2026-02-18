package com.advann.order_service.service.services;

import com.advann.order_service.dto.OrderResponseDto;
import com.advann.order_service.dto.PaymentStatusUpdateRequestDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto placeOrder(Long userId);

    OrderResponseDto getOrderById(Long orderId);

    List<OrderResponseDto> getOrdersByUserId(Long userId);

    OrderResponseDto cancelOrder(Long orderId);

    OrderResponseDto updatePaymentStatus(Long orderId, PaymentStatusUpdateRequestDto requestDto);
}