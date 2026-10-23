package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Level 1 in-memory implementation of {@link ProductRepository} backed by a {@link HashMap}.
 */
public class InMemoryProductRepository implements ProductRepository {

    private final Map<ProductId, Product> products = new HashMap<>();

    @Override
    public Product save(Product product) {
        Objects.requireNonNull(product, "product must not be null");
        products.put(product.id(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }
}
