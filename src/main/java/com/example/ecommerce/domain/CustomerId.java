package com.example.ecommerce.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly-typed identifier for a {@link Customer}.
 */
public record CustomerId(String value) {

    public CustomerId {
        Objects.requireNonNull(value, "CustomerId value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CustomerId value must not be blank");
        }
    }

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    public static CustomerId generate() {
        return new CustomerId("CUST-" + UUID.randomUUID().toString().substring(0, 8));
    }

    @Override
    public String toString() {
        return value;
    }
}
