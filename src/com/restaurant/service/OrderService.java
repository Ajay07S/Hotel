
package com.restaurant.service;

import com.restaurant.models.MenuItem;
import com.restaurant.models.Order;
import com.restaurant.models.Payment;
import com.restaurant.models.RestaurantTable;

import com.restaurant.repository.MenuRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import com.restaurant.repository.TableRepository;

import java.util.List;

public class OrderService {

    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(
            MenuRepository menuRepository,
            OrderRepository orderRepository,
            TableRepository tableRepository,
            PaymentRepository paymentRepository) {

        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.paymentRepository = paymentRepository;
    }

    public Order createOrder(int tableId) {

        if (tableId <= 0) {
            throw new IllegalArgumentException("Invalid table ID");
        }
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Table not found"));

        if (table.getStatus() != RestaurantTable.TableStatus.AVAILABLE) {
            throw new IllegalStateException("Table is not available");
        }

        Order order = orderRepository.save(tableId);

        table.setStatus(RestaurantTable.TableStatus.OCCUPIED);

        return order;
    }

    public void addItem(int orderId, int menuItemId, int quantity) {

        Order order = getOrderById(orderId);

        if (order.getStatus() == Order.OrderStatus.CANCELLED
                || order.getStatus() == Order.OrderStatus.COMPLETED
                || order.getStatus() == Order.OrderStatus.PAID
                || order.getStatus() == Order.OrderStatus.PREPARING
                || order.getStatus() == Order.OrderStatus.READY) {
            throw new IllegalStateException(
                    "Cannot modify this order");
        }

        if (paymentRepository.findByOrderId(orderId)
                .filter(p -> p.getStatus()
                        == Payment.PaymentStatus.SUCCESS)
                .isPresent()) {
            throw new IllegalStateException(
                    "Order has already been paid");
        }

        if (menuItemId <= 0) {
            throw new IllegalArgumentException("Invalid menu item ID");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        MenuItem item = menuRepository.findById(menuItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Menu item not found"));

        if (!item.isAvailable()) {
            throw new IllegalStateException("Menu item is unavailable");
        }

        order.addItem(item, quantity);
    }

    public double getOrderTotal(int orderId) {
        return getOrderById(orderId).calculateTotal();
    }

    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void cancelOrder(int orderId) {

        Order order = getOrderById(orderId);

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        if (order.getStatus() != Order.OrderStatus.PENDING
                && order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "This order cannot be cancelled in its current status");
        }

        if (paymentRepository.findByOrderId(orderId)
                .filter(p -> p.getStatus()
                        == Payment.PaymentStatus.SUCCESS)
                .isPresent()) {
            throw new IllegalStateException(
                    "Paid order needs a refund before cancellation");
        }

        RestaurantTable table = tableRepository.findById(order.getTableId())
                .orElseThrow(() ->
                        new IllegalStateException("Table not found"));

        order.setStatus(Order.OrderStatus.CANCELLED);
        table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
    }

    public void markCompleted(int orderId) {

        Order order = getOrderById(orderId);

        if (order.getStatus() != Order.OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Only paid orders can be completed");
        }

        RestaurantTable table = tableRepository.findById(order.getTableId())
                .orElseThrow(() ->
                        new IllegalStateException("Table not found"));

        order.setStatus(Order.OrderStatus.COMPLETED);
        table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
    }

    public void startPreparing(int orderId) {
        Order order = getOrderById(orderId);
        order.setStatus(Order.OrderStatus.PREPARING);
    }

    public void markReady(int orderId) {
        Order order = getOrderById(orderId);
        order.setStatus(Order.OrderStatus.READY);
    }
}
