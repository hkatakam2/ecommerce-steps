package com.example.ecommerce.domain;

import java.util.Objects;

/**
 * An immutable line item within an {@link Order}, capturing the product snapshot
 * (name and unit price) at the time the item was added.
 */
public record OrderItem(ProductId productId, String productName, Money unitPrice, int quantity) {

    public OrderItem {
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(productName, "productName must not be null");
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        if (productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("OrderItem quantity must be positive: " + quantity);
        }
    }

    public static OrderItem from(Product product, int quantity) {
        Objects.requireNonNull(product, "product must not be null");
        return new OrderItem(product.id(), product.name(), product.price(), quantity);
    }

    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }
}
