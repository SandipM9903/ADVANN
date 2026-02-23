package com.advann.order_service.enums;

public enum OrderStatus {
    CREATED,        // order placed, payment pending
    CONFIRMED,      // payment success
    SHIPPED,
    DELIVERED,
    CANCELLED
}