package com.example.ecommerce;

public class App {

    public record Product(String id, String name, double price) {}

    public static String greet(String name) {
        return "Welcome to %s!".formatted(name);
    }

    public static void main(String[] args) {
        System.out.println(greet("Ecommerce Steps"));
        var sample = new Product("P-001", "Wireless Headphones", 99.99);
        System.out.println("Featured product: " + sample);
    }
}
