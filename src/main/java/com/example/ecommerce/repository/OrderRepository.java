package com.example.ecommerce.repository;

import com.example.ecommerce.domain.CustomerId;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Repository contract for storing and retrieving {@link Order}s.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId id);

    List<Order> findByCustomerId(CustomerId customerId);

    List<Order> findAll();
}
