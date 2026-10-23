package com.example.ecommerce.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly-typed identifier for an {@link Order}.
 */
public record OrderId(String value) {

    public OrderId {
        Objects.requireNonNull(value, "OrderId value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("OrderId value must not be blank");
        }
    }

    public static OrderId of(String value) {
        return new OrderId(value);
    }

    public static OrderId generate() {
        return new OrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8));
    }

    @Override
    public String toString() {
        return value;
    }
}
