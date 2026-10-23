package com.example.ecommerce;

import com.example.ecommerce.domain.Money;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void shouldReturnGreeting() {
        assertEquals("Welcome to Ecommerce Steps!", App.greet("Ecommerce Steps"));
    }

    @Test
    void shouldCreateProductRecord() {
        var product = new Product(ProductId.of("P-001"), "Keyboard", Money.of("79.50"));
        assertEquals(ProductId.of("P-001"), product.id());
        assertEquals("Keyboard", product.name());
        assertEquals(Money.of("79.50"), product.price());
    }
}
