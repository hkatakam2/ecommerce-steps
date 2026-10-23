package com.example.ecommerce.service;

import com.example.ecommerce.domain.CustomerId;
import com.example.ecommerce.domain.Money;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderId;
import com.example.ecommerce.domain.OrderNotFoundException;
import com.example.ecommerce.domain.OrderStatus;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductId;
import com.example.ecommerce.domain.ProductNotFoundException;
import com.example.ecommerce.repository.InMemoryOrderRepository;
import com.example.ecommerce.repository.InMemoryProductRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {

    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private OrderService orderService;

    private Product laptop;
    private Product headphones;
    private CustomerId customerId;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        orderRepository = new InMemoryOrderRepository();
        orderService = new OrderService(productRepository, orderRepository);

        laptop = productRepository.save(new Product(
                ProductId.of("PROD-LAPTOP"),
                "MacBook Pro",
                Money.of("1999.00")
        ));
        headphones = productRepository.save(new Product(
                ProductId.of("PROD-HEADPHONES"),
                "Studio Headphones",
                Money.of("299.50")
        ));
        customerId = CustomerId.of("CUST-ALICE");
    }

    @Test
    void shouldCreateAndPersistOrderInMemory() {
        Order order = orderService.createOrder(customerId, laptop.id(), 2);

        assertNotNull(order.id());
        assertEquals(customerId, order.customerId());
        assertEquals(OrderStatus.CREATED, order.status());
        assertEquals(Money.of("3998.00"), order.totalAmount());

        Order fetched = orderService.getOrder(order.id());
        assertEquals(order.id(), fetched.id());
        assertEquals(Money.of("3998.00"), fetched.totalAmount());
    }

    @Test
    void shouldAddItemsAndPayOrder() {
        Order order = orderService.createOrder(customerId, laptop.id(), 1);
        orderService.addItem(order.id(), headphones.id(), 2);
        Order paidOrder = orderService.payOrder(order.id());

        assertEquals(OrderStatus.PAID, paidOrder.status());
        assertEquals(2, paidOrder.items().size());
        assertEquals(Money.of("2598.00"), paidOrder.totalAmount());
    }

    @Test
    void shouldCancelCreatedOrder() {
        Order order = orderService.createOrder(customerId, laptop.id(), 1);
        Order cancelled = orderService.cancelOrder(order.id());

        assertEquals(OrderStatus.CANCELLED, cancelled.status());
    }

    @Test
    void shouldThrowWhenCreatingOrderForUnknownProduct() {
        ProductId unknownId = ProductId.of("PROD-UNKNOWN");

        ProductNotFoundException ex = assertThrows(
                ProductNotFoundException.class,
                () -> orderService.createOrder(customerId, unknownId, 1)
        );
        assertEquals(unknownId, ex.productId());
    }

    @Test
    void shouldThrowWhenLookingUpUnknownOrder() {
        OrderId unknownOrderId = OrderId.of("ORD-UNKNOWN");

        OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrder(unknownOrderId)
        );
        assertEquals(unknownOrderId, ex.orderId());
    }

    @Test
    void shouldFindOrdersByCustomerId() {
        CustomerId otherCustomer = CustomerId.of("CUST-BOB");
        orderService.createOrder(customerId, laptop.id(), 1);
        orderService.createOrder(customerId, headphones.id(), 1);
        orderService.createOrder(otherCustomer, laptop.id(), 1);

        List<Order> aliceOrders = orderService.getOrdersForCustomer(customerId);
        assertEquals(2, aliceOrders.size());
    }
}
