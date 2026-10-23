package com.example.ecommerce.domain;

/**
 * Thrown when a requested {@link Order} does not exist.
 */
public class OrderNotFoundException extends RuntimeException {

    private final OrderId orderId;

    public OrderNotFoundException(OrderId orderId) {
        super("Order not found: " + orderId);
        this.orderId = orderId;
    }

    public OrderId orderId() {
        return orderId;
    }
}
