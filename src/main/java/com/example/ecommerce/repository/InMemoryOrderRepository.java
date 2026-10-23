package com.example.ecommerce.repository;

import com.example.ecommerce.domain.CustomerId;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Level 1 in-memory implementation of {@link OrderRepository} backed by a {@link HashMap}.
 */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> orders = new HashMap<>();

    @Override
    public Order save(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        orders.put(order.id(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        return orders.values().stream()
                .filter(order -> order.customerId().equals(customerId))
                .toList();
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orders.values());
    }
}
