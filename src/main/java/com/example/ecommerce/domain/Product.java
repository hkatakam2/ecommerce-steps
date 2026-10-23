package com.example.ecommerce.domain;

import java.util.Objects;

/**
 * Represents a sellable product in the catalog.
 */
public record Product(ProductId id, String name, Money price) {

    public Product {
        Objects.requireNonNull(id, "Product id must not be null");
        Objects.requireNonNull(name, "Product name must not be null");
        Objects.requireNonNull(price, "Product price must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
    }
}
