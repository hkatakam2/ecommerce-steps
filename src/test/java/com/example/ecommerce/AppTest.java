package com.example.ecommerce;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void shouldReturnGreeting() {
        assertEquals("Welcome to Ecommerce Steps!", App.greet("Ecommerce Steps"));
    }

    @Test
    void shouldCreateProductRecord() {
        var product = new App.Product("P-001", "Keyboard", 79.50);
        assertEquals("P-001", product.id());
        assertEquals("Keyboard", product.name());
        assertEquals(79.50, product.price());
    }
}
