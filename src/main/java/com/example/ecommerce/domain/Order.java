package com.example.ecommerce.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing a customer order and enforcing order-level business invariants.
 */
public class Order {

    private final OrderId id;
    private final CustomerId customerId;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private OrderStatus status;

    private Order(OrderId id, CustomerId customerId, List<OrderItem> items, OrderStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Order id must not be null");
        this.customerId = Objects.requireNonNull(customerId, "CustomerId must not be null");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "items must not be null"));
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static Order create(CustomerId customerId) {
        return new Order(OrderId.generate(), customerId, List.of(), OrderStatus.CREATED, Instant.now());
    }

    public static Order create(CustomerId customerId, Product product, int quantity) {
        Order order = create(customerId);
        order.addItem(product, quantity);
        return order;
    }

    public void addItem(Product product, int quantity) {
        Objects.requireNonNull(product, "product must not be null");
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add items to an order in status " + status);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }

        for (int i = 0; i < items.size(); i++) {
            OrderItem existing = items.get(i);
            if (existing.productId().equals(product.id())) {
                items.set(i, new OrderItem(
                        product.id(),
                        product.name(),
                        product.price(),
                        existing.quantity() + quantity
                ));
                return;
            }
        }

        items.add(OrderItem.from(product, quantity));
    }

    public void markPaid() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be marked as PAID, current status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot pay for an order with no items");
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel an order that has already been PAID");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public Money totalAmount() {
        return items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::add);
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public List<OrderItem> items() {
        return List.copyOf(items);
    }

    public OrderStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Order[id=%s, customerId=%s, status=%s, items=%d, total=%s]".formatted(
                id, customerId, status, items.size(), totalAmount()
        );
    }
}
