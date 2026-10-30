
package com.restaurant.models;

public class RestaurantTable {

    public enum TableStatus {
        AVAILABLE,
        OCCUPIED,
        RESERVED
    }

    private final int id;
    private final int capacity;
    private TableStatus status;

    public RestaurantTable(int id, int capacity) {

        if (id <= 0 || capacity <= 0) {
            throw new IllegalArgumentException(
                "Invalid table ID or capacity"
            );
        }

        this.id = id;
        this.capacity = capacity;
        this.status = TableStatus.AVAILABLE;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                "Table status cannot be null"
            );
        }

        this.status = status;
    }
}
