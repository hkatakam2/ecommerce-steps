package com.example.ecommerce;

import com.example.ecommerce.domain.Customer;
import com.example.ecommerce.domain.CustomerId;
import com.example.ecommerce.domain.Money;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductId;
import com.example.ecommerce.repository.InMemoryOrderRepository;
import com.example.ecommerce.repository.InMemoryProductRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderService;

public class App {

    public static String greet(String name) {
        return "Welcome to %s!".formatted(name);
    }

    public static void main(String[] args) {
        System.out.println(greet("Ecommerce Steps - Level 1 (In-Memory Domain Model)"));
        System.out.println("=".repeat(60));

        // 1. Wire repositories (HashMap-backed) and service
        ProductRepository productRepository = new InMemoryProductRepository();
        OrderRepository orderRepository = new InMemoryOrderRepository();
        OrderService orderService = new OrderService(productRepository, orderRepository);

        // 2. Seed product catalog
        Product laptop = productRepository.save(new Product(
                ProductId.of("PROD-LAPTOP"),
                "MacBook Pro 16\"",
                Money.of("1999.00")
        ));
        Product headphones = productRepository.save(new Product(
                ProductId.of("PROD-HEADPHONES"),
                "Wireless Noise-Cancelling Headphones",
                Money.of("299.50")
        ));

        System.out.println("Catalog products:");
        for (Product product : productRepository.findAll()) {
            System.out.printf("  - [%s] %s (%s)%n", product.id(), product.name(), product.price());
        }

        // 3. Create a customer and place an order
        Customer alice = new Customer(CustomerId.of("CUST-ALICE"), "Alice Smith", "alice@example.com");
        System.out.printf("%nCustomer: %s (%s)%n", alice.name(), alice.id());

        Order order = orderService.createOrder(alice.id(), laptop.id(), 1);
        orderService.addItem(order.id(), headphones.id(), 2);
        orderService.payOrder(order.id());

        // 4. Retrieve and print the stored order
        Order storedOrder = orderService.getOrder(order.id());
        System.out.printf("%nOrder Summary: %s (Status: %s)%n", storedOrder.id(), storedOrder.status());
        for (OrderItem item : storedOrder.items()) {
            System.out.printf("  * %d x %s @ %s = %s%n",
                    item.quantity(), item.productName(), item.unitPrice(), item.subtotal());
        }
        System.out.printf("Total: %s%n", storedOrder.totalAmount());
    }
}
