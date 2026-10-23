package com.example.ecommerce.service;

import com.example.ecommerce.domain.CustomerId;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderId;
import com.example.ecommerce.domain.OrderNotFoundException;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductId;
import com.example.ecommerce.domain.ProductNotFoundException;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;

import java.util.List;
import java.util.Objects;

/**
 * Application service coordinating order creation and lifecycle transitions
 * across {@link ProductRepository} and {@link OrderRepository}.
 */
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = Objects.requireNonNull(productRepository, "productRepository must not be null");
        this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
    }

    public Order createOrder(CustomerId customerId, ProductId productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Order order = Order.create(customerId, product, quantity);
        return orderRepository.save(order);
    }

    public Order placeOrder(CustomerId customerId, ProductId productId, int quantity) {
        return createOrder(customerId, productId, quantity);
    }

    public Order addItem(OrderId orderId, ProductId productId, int quantity) {
        Order order = getOrder(orderId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        order.addItem(product, quantity);
        return orderRepository.save(order);
    }

    public Order payOrder(OrderId orderId) {
        Order order = getOrder(orderId);
        order.markPaid();
        return orderRepository.save(order);
    }

    public Order cancelOrder(OrderId orderId) {
        Order order = getOrder(orderId);
        order.cancel();
        return orderRepository.save(order);
    }

    public Order getOrder(OrderId orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<Order> getOrdersForCustomer(CustomerId customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}
