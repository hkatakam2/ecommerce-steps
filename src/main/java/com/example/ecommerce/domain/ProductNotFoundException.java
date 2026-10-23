package com.example.ecommerce.domain;

/**
 * Thrown when a requested {@link Product} does not exist in the catalog.
 */
public class ProductNotFoundException extends RuntimeException {

    private final ProductId productId;

    public ProductNotFoundException(ProductId productId) {
        super("Product not found: " + productId);
        this.productId = productId;
    }

    public ProductId productId() {
        return productId;
    }
}
