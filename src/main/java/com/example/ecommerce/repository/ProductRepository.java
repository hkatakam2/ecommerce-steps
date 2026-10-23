package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * Repository contract for storing and retrieving {@link Product}s.
 */
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(ProductId id);

    List<Product> findAll();
}
