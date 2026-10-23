package com.example.ecommerce.domain;

import java.util.Objects;

/**
 * Represents a customer who can place orders.
 */
public record Customer(CustomerId id, String name, String email) {

    public Customer {
        Objects.requireNonNull(id, "Customer id must not be null");
        Objects.requireNonNull(name, "Customer name must not be null");
        Objects.requireNonNull(email, "Customer email must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be blank");
        }
        if (email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Customer email must be valid: " + email);
        }
    }
}
