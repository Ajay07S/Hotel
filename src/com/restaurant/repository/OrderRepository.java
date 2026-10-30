
package com.restaurant.repository;

import com.restaurant.models.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {

    private final List<Order> orders = new ArrayList<>();
    private int nextId = 1;

    public Order save(int tableId) {
        Order order = new Order(nextId++, tableId);
        orders.add(order);
        return order;
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders);
    }

    public Optional<Order> findById(int id) {
        return orders.stream()
            .filter(order -> order.getId() == id)
            .findFirst();
    }
}
