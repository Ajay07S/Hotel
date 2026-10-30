
package com.restaurant.models;

public class MenuItem {

    private final int id;
    private String name;
    private double price;
    private String category;
    private boolean available;

    public MenuItem(int id, String name, double price, String category) {
        if (id <= 0) {
            throw new IllegalArgumentException("Menu item ID must be positive");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Menu item name is required");
        }
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Price must be a finite, non-negative amount");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Menu item category is required");
        }

        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setPrice(double price) {
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                "Price must be a finite, non-negative amount"
            );
        }
        this.price = price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
