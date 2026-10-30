
package com.restaurant.models;

public class OrderItem {

    private final MenuItem menuItem;
    private final double unitPrice;
    private int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {

        if (menuItem == null) {
            throw new IllegalArgumentException(
                "Menu item cannot be null"
            );
        }

        if (!menuItem.isAvailable()) {
            throw new IllegalArgumentException(
                "Menu item is unavailable"
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero"
            );
        }

        this.menuItem = menuItem;
        this.unitPrice = menuItem.getPrice();
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero"
            );
        }

        this.quantity = quantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
