
package com.restaurant.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PREPARING,
        READY,
        PAID,
        COMPLETED,
        CANCELLED
    }

    private final int id;
    private final int tableId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(int id, int tableId) {

        if (id <= 0 || tableId <= 0) {
            throw new IllegalArgumentException(
                "Invalid order ID or table ID"
            );
        }

        this.id = id;
        this.tableId = tableId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(MenuItem menuItem, int quantity) {

        if (status != OrderStatus.PENDING
                && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Items can only be added to pending or confirmed orders"
            );
        }

        if (menuItem == null || !menuItem.isAvailable()) {
            throw new IllegalArgumentException("Menu item is unavailable");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        for (OrderItem item : items) {
            if (item.getMenuItem().getId() == menuItem.getId()) {
                if (quantity > Integer.MAX_VALUE - item.getQuantity()) {
                    throw new IllegalArgumentException(
                        "Combined item quantity exceeds the supported limit"
                    );
                }
                item.setQuantity(item.getQuantity() + quantity);
                status = OrderStatus.CONFIRMED;
                return;
            }
        }

        items.add(new OrderItem(menuItem, quantity));
        status = OrderStatus.CONFIRMED;
    }

    public double calculateTotal() {

        double total = 0;

        for (OrderItem item : items) {
            total += item.getSubtotal();
        }

        return total;
    }

    public int getId() {
        return id;
    }

    public int getTableId() {
        return tableId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                "Status cannot be null"
            );
        }
        boolean validTransition =
            (this.status == OrderStatus.PENDING
                && (status == OrderStatus.CONFIRMED
                    || status == OrderStatus.CANCELLED))
            || (this.status == OrderStatus.CONFIRMED
                && (status == OrderStatus.PREPARING
                    || status == OrderStatus.PAID
                    || status == OrderStatus.CANCELLED))
            || (this.status == OrderStatus.PREPARING
                && (status == OrderStatus.READY
                    || status == OrderStatus.PAID))
            || (this.status == OrderStatus.READY
                && status == OrderStatus.PAID)
            || (this.status == OrderStatus.PAID
                && status == OrderStatus.COMPLETED);
        if (!validTransition) {
            throw new IllegalStateException(
                "Invalid order status transition from " + this.status + " to " + status
            );
        }
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
