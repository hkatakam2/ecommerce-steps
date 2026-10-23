# Level 1 — Java Domain Model & In-Memory Order Service

## 1. Where we are

Previously (Level 0), the project was a minimal Java 21 starter with a single `App.java` containing a placeholder `Product(String id, String name, double price)` record and a greeting method.

## 2. The new requirement

We need to model the core e-commerce business workflow inside a single JVM before introducing HTTP frameworks or databases:

1. Store a catalog of sellable products (`Product`).
2. Allow a customer (`Customer`) to create an `Order`, add line items (`OrderItem`), calculate the order total accurately, and transition the order through its lifecycle (`CREATED`, `PAID`, `CANCELLED`).
3. Coordinate product lookup and order persistence through an `OrderService` backed by in-memory `HashMap` repositories.

## 3. Why the existing design fails

The starter `record Product(String id, String name, double price)` had three fundamental flaws for a real backend system:

1. **Floating-point money (`double`)**: Binary floating-point cannot represent `0.10` or `19.99` exactly (`0.1 + 0.2 == 0.30000000000000004`), causing rounding drift across line items.
2. **Primitive obsession (`String id`)**: When `productId`, `customerId`, and `orderId` are all raw `String` parameters, the compiler cannot stop a caller from accidentally passing `createOrder(productId, customerId, quantity)`.
3. **No separation of responsibilities**: Business rules, data storage, and application coordination were all absent or mixed in `main`.

## 4. Concepts we need

- **Value Objects (`Money`, `ProductId`, `CustomerId`, `OrderId`, `OrderItem`)**: Immutable types defined by their values rather than identity, implemented with Java `record`s and compact constructors that enforce invariants on creation.
- **Aggregate Root (`Order`)**: Encapsulates mutable state transitions (`addItem`, `markPaid`, `cancel`) and returns defensive copies (`List.copyOf(items)`) so external callers cannot bypass invariants.
- **Repository Interfaces (`ProductRepository`, `OrderRepository`)**: Decouple `OrderService` from how objects are stored (`HashMap` today, PostgreSQL in Level 3).
- **Domain Exceptions (`ProductNotFoundException`, `OrderNotFoundException`)**: Express business failures explicitly rather than returning `null`.

## 5. Design

```text
Main (App)
 |
OrderService
 |
 +-- ProductRepository (InMemoryProductRepository -> HashMap<ProductId, Product>)
 +-- OrderRepository   (InMemoryOrderRepository   -> HashMap<OrderId, Order>)
```

Key architectural decisions:
- **`OrderItem` snapshots product name and unit price**: If a product's catalog price changes tomorrow, historical orders retain the price at the time the item was added.
- **`OrderService` depends on interfaces, not `HashMap`**: `OrderService` accepts `ProductRepository` and `OrderRepository` via constructor injection, preparing the codebase for Spring dependency injection in Level 2 and database persistence in Level 3.

## 6. Actual code

- Domain model: `src/main/java/com/example/ecommerce/domain/`
  - `Money`, `ProductId`, `CustomerId`, `OrderId`
  - `Product`, `Customer`, `OrderItem`, `OrderStatus`, `Order`
  - `ProductNotFoundException`, `OrderNotFoundException`
- Repositories: `src/main/java/com/example/ecommerce/repository/`
  - `ProductRepository`, `InMemoryProductRepository`
  - `OrderRepository`, `InMemoryOrderRepository`
- Service: `src/main/java/com/example/ecommerce/service/OrderService.java`

## 7. Request walkthrough

When `orderService.createOrder(customerId, productId, quantity)` is called:

```text
App.main()
   |
   | createOrder(CUST-ALICE, PROD-LAPTOP, 1)
   v
OrderService
   |
   +--> productRepository.findById(PROD-LAPTOP)
   |       |
   |       +--> HashMap.get(PROD-LAPTOP) -> Optional<Product>
   |
   +--> Order.create(CUST-ALICE, laptop, 1)
   |       |
   |       +--> validates quantity > 0
   |       +--> snapshots OrderItem(PROD-LAPTOP, "MacBook Pro 16\"", $1999.00, 1)
   |
   +--> orderRepository.save(order)
           |
           +--> HashMap.put(order.id(), order)
```

## 8. Failure scenarios

- **Unknown `ProductId`**: `OrderService` throws `ProductNotFoundException` before any `Order` is created.
- **Unknown `OrderId`**: `OrderService.getOrder` throws `OrderNotFoundException`.
- **Invalid quantities or negative money**: Rejected immediately by `Order` / `OrderItem` / `Money` constructors with `IllegalArgumentException`.
- **Illegal lifecycle transitions**: Adding items to or cancelling a `PAID` order throws `IllegalStateException`.
- **Process crash or restart**: Because `InMemoryProductRepository` and `InMemoryOrderRepository` store state in a JVM `HashMap`, all products and orders vanish when the process exits.

## 9. Tests

- `OrderTest`: Verifies multi-item total calculation, quantity merging for duplicate products, negative money rejection, state transition guards after payment, and unmodifiable item lists.
- `OrderServiceTest`: Verifies end-to-end order creation, item addition, payment, cancellation, customer order lookup, and `ProductNotFoundException` / `OrderNotFoundException` handling.

## 10. What changed from the previous version

```text
Level 0:
App (single file with record Product(String, String, double))

Level 1:
App
 |
OrderService
 |
 +-- ProductRepository -> InMemoryProductRepository (HashMap)
 +-- OrderRepository   -> InMemoryOrderRepository   (HashMap)
```

## 11. What we learned

- **Before**: Domain data is a bag of primitives (`String`, `double`) manipulated anywhere.
- **After**: Domain objects protect their own invariants, money uses fixed-scale `BigDecimal`, identifiers are strongly typed, and `OrderService` coordinates domain objects through repository interfaces.

## 12. What problem remains

Our order system only runs inside `App.main()` and exits immediately. No external client (browser, mobile app, or another service) can call `OrderService` over the network. That motivates **Level 2: Turning the application into an HTTP service with Spring Boot**.
