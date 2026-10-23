package com.example.ecommerce.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly-typed identifier for a {@link Product}.
 */
public record ProductId(String value) {

    public ProductId {
        Objects.requireNonNull(value, "ProductId value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ProductId value must not be blank");
        }
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }

    public static ProductId generate() {
        return new ProductId("PROD-" + UUID.randomUUID().toString().substring(0, 8));
    }

    @Override
    public String toString() {
        return value;
    }
}
