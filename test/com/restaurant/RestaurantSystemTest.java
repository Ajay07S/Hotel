package com.restaurant;

import com.restaurant.models.MenuItem;
import com.restaurant.models.Order;
import com.restaurant.models.Payment;
import com.restaurant.models.RestaurantTable;
import com.restaurant.repository.MenuRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import com.restaurant.repository.TableRepository;
import com.restaurant.service.OrderService;
import com.restaurant.service.PaymentService;
import com.restaurant.service.TableService;

public class RestaurantSystemTest {

    public static void main(String[] args) {
        MenuRepository menuRepository = new MenuRepository();
        TableRepository tableRepository = new TableRepository();
        OrderRepository orderRepository = new OrderRepository();
        PaymentRepository paymentRepository = new PaymentRepository();
        OrderService orderService = new OrderService(
            menuRepository,
            orderRepository,
            tableRepository,
            paymentRepository
        );
        PaymentService paymentService = new PaymentService(
            orderRepository,
            paymentRepository
        );
        TableService tableService = new TableService(tableRepository);

        Order order = orderService.createOrder(1);
        expectThrows(IllegalStateException.class,
            () -> orderService.createOrder(1));
        expectThrows(IllegalStateException.class,
            () -> tableService.updateTableStatus(
                1, RestaurantTable.TableStatus.AVAILABLE));

        orderService.addItem(order.getId(), 1, 2);
        orderService.addItem(order.getId(), 1, 1);
        check(order.getItems().size() == 1, "Repeated item should merge");
        check(order.getItems().get(0).getQuantity() == 3,
            "Merged item quantity should be three");
        check(orderService.getOrderTotal(order.getId()) == 540.0,
            "Order total should include merged quantity");

        menuRepository.findById(1).orElseThrow().setPrice(200.0);
        check(orderService.getOrderTotal(order.getId()) == 540.0,
            "Existing order should retain its item price");

        menuRepository.findById(2).orElseThrow().setAvailable(false);
        expectThrows(IllegalStateException.class,
            () -> orderService.addItem(order.getId(), 2, 1));
        expectThrows(IllegalArgumentException.class,
            () -> orderService.addItem(order.getId(), 1, 0));

        orderService.startPreparing(order.getId());
        orderService.markReady(order.getId());
        expectThrows(IllegalStateException.class,
            () -> orderService.cancelOrder(order.getId()));

        Payment payment = paymentService.makePayment(
            order.getId(), Payment.PaymentMethod.UPI);
        check(payment.getAmount() == 540.0, "Payment should use order total");
        check(payment.getStatus() == Payment.PaymentStatus.SUCCESS,
            "Simulated payment should succeed");
        check(order.getStatus() == Order.OrderStatus.PAID,
            "Successful payment should mark order paid");
        expectThrows(IllegalStateException.class,
            () -> paymentService.makePayment(
                order.getId(), Payment.PaymentMethod.CASH));

        orderService.markCompleted(order.getId());
        check(tableRepository.findById(1).orElseThrow().getStatus()
                == RestaurantTable.TableStatus.AVAILABLE,
            "Completing a paid order should release its table");

        Order emptyOrder = orderService.createOrder(2);
        expectThrows(IllegalStateException.class,
            () -> paymentService.makePayment(
                emptyOrder.getId(), Payment.PaymentMethod.CARD));
        orderService.cancelOrder(emptyOrder.getId());
        check(tableRepository.findById(2).orElseThrow().getStatus()
                == RestaurantTable.TableStatus.AVAILABLE,
            "Cancelling an order should release its table");
        expectThrows(IllegalStateException.class,
            () -> orderService.cancelOrder(emptyOrder.getId()));
        expectThrows(IllegalStateException.class,
            () -> paymentService.makePayment(
                emptyOrder.getId(), Payment.PaymentMethod.CASH));
        expectThrows(IllegalArgumentException.class,
            () -> orderService.createOrder(0));
        expectThrows(IllegalStateException.class,
            () -> new Order(99, 99).setStatus(Order.OrderStatus.PAID));
        expectThrows(IllegalArgumentException.class,
            () -> new MenuItem(0, "Invalid", 1.0, "Test"));
        expectThrows(IllegalArgumentException.class,
            () -> new MenuItem(99, "Invalid", Double.NaN, "Test"));

        System.out.println("All restaurant system tests passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectThrows(
            Class<? extends RuntimeException> exceptionType,
            Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (exceptionType.isInstance(exception)) {
                return;
            }
            throw new AssertionError(
                "Expected " + exceptionType.getSimpleName()
                    + " but got " + exception.getClass().getSimpleName(),
                exception
            );
        }
        throw new AssertionError(
            "Expected " + exceptionType.getSimpleName()
        );
    }
}
