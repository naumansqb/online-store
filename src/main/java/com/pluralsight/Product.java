package com.pluralsight;

public class Product {
    private String id;
    private String description;
    private double price;

    public Product(double price, String id, String description) {
        this.price = price;
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }


    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%-12s | %-40s | $%,.2f", id, description, price);
    }
}