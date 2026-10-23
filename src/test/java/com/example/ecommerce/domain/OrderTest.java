package com.example.ecommerce.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    private static final CustomerId CUSTOMER_ID = CustomerId.of("CUST-1");
    private static final Product LAPTOP = new Product(
            ProductId.of("PROD-1"),
            "MacBook Pro",
            Money.of("1999.00")
    );
    private static final Product MOUSE = new Product(
            ProductId.of("PROD-2"),
            "Magic Mouse",
            Money.of("99.50")
    );

    @Test
    void shouldCalculateTotalAcrossMultipleItems() {
        Order order = Order.create(CUSTOMER_ID, LAPTOP, 1);
        order.addItem(MOUSE, 2);

        assertEquals(2, order.items().size());
        assertEquals(Money.of("2198.00"), order.totalAmount());
        assertEquals(OrderStatus.CREATED, order.status());
    }

    @Test
    void shouldMergeQuantitiesWhenAddingSameProductTwice() {
        Order order = Order.create(CUSTOMER_ID, MOUSE, 1);
        order.addItem(MOUSE, 3);

        assertEquals(1, order.items().size());
        assertEquals(4, order.items().getFirst().quantity());
        assertEquals(Money.of("398.00"), order.totalAmount());
    }

    @Test
    void shouldRejectNonPositiveItemQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> Order.create(CUSTOMER_ID, LAPTOP, 0));
        assertThrows(IllegalArgumentException.class,
                () -> Order.create(CUSTOMER_ID, LAPTOP, -2));
    }

    @Test
    void shouldRejectNegativeMoney() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.of("-0.01"));
    }

    @Test
    void shouldPreventModifyingItemsAfterOrderIsPaid() {
        Order order = Order.create(CUSTOMER_ID, LAPTOP, 1);
        order.markPaid();

        assertEquals(OrderStatus.PAID, order.status());
        assertThrows(IllegalStateException.class, () -> order.addItem(MOUSE, 1));
        assertThrows(IllegalStateException.class, order::cancel);
    }

    @Test
    void shouldExposeUnmodifiableItemsList() {
        Order order = Order.create(CUSTOMER_ID, LAPTOP, 1);

        assertThrows(UnsupportedOperationException.class,
                () -> order.items().clear());
    }
}
