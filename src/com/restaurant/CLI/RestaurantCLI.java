
package com.restaurant.CLI;

import com.restaurant.models.MenuItem;
import com.restaurant.models.Order;
import com.restaurant.models.Payment;
import com.restaurant.models.RestaurantTable;

import com.restaurant.repository.MenuRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import com.restaurant.repository.TableRepository;

import com.restaurant.service.MenuService;
import com.restaurant.service.OrderService;
import com.restaurant.service.PaymentService;
import com.restaurant.service.TableService;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class RestaurantCLI {

    private final Scanner scanner = new Scanner(System.in);

    // Repositories
    private final MenuRepository menuRepository =
            new MenuRepository();

    private final TableRepository tableRepository =
            new TableRepository();

    private final OrderRepository orderRepository =
            new OrderRepository();

    private final PaymentRepository paymentRepository =
            new PaymentRepository();

    // Services
    private final MenuService menuService =
            new MenuService(menuRepository);

    private final TableService tableService =
            new TableService(tableRepository);

    private final OrderService orderService =
            new OrderService(
                menuRepository,
                orderRepository,
                tableRepository,
                paymentRepository
            );

    private final PaymentService paymentService =
            new PaymentService(
                orderRepository,
                paymentRepository
            );

    // Main menu
    public void start() {

        while (true) {

            System.out.println("\n===== RESTAURANT MANAGEMENT SYSTEM =====");
            System.out.println("1. View Menu");
            System.out.println("2. View Tables");
            System.out.println("3. Create Order");
            System.out.println("4. View Orders");
            System.out.println("5. Make Payment");
            System.out.println("6. Cancel Order");
            System.out.println("7. Complete Order");
            System.out.println("8. Exit");

            try {
                int choice = readInt("Enter your choice: ");
                switch (choice) {

                    case 1:
                        viewMenu();
                        break;

                    case 2:
                        viewTables();
                        break;

                    case 3:
                        createOrder();
                        break;

                    case 4:
                        viewOrders();
                        break;

                    case 5:
                        makePayment();
                        break;

                    case 6:
                        cancelOrder();
                        break;

                    case 7:
                        completeOrder();
                        break;

                    case 8:
                        System.out.println("Thank you!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (IllegalArgumentException
                     | IllegalStateException e) {

                System.out.println("Error: " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("\nInput closed. Thank you!");
                return;
            }
        }
    }

    // Safely read integer input
    private int readInt(String message) {
        while (true) {
            System.out.print(message);
            if (!scanner.hasNextLine()) {
                throw new NoSuchElementException("Input closed");
            }
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // Display menu
    private void viewMenu() {

        System.out.println("\n===== FOOD MENU =====");

        for (MenuItem item : menuService.getAllMenuItems()) {

            System.out.printf(
                "ID: %d | Name: %s | Price: Rs. %.2f"
                + " | Category: %s | Available: %s%n",
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getCategory(),
                item.isAvailable()
            );
        }
    }

    // Display tables
    private void viewTables() {

        System.out.println("\n===== RESTAURANT TABLES =====");

        for (RestaurantTable table : tableService.getAllTables()) {

            System.out.printf(
                "Table ID: %d | Capacity: %d | Status: %s%n",
                table.getId(),
                table.getCapacity(),
                table.getStatus()
            );
        }
    }

    // Create an order and add food items
    private void createOrder() {

        viewTables();

        int tableId = readInt("Enter table ID: ");

        Order order = orderService.createOrder(tableId);

        System.out.println(
            "Order created successfully! Order ID: "
            + order.getId()
        );

        while (true) {

            viewMenu();

            int menuItemId =
                    readInt("Enter food ID (0 to finish): ");

            if (menuItemId == 0) {
                break;
            }

            int quantity = readInt("Enter quantity: ");

            try {
                orderService.addItem(
                    order.getId(),
                    menuItemId,
                    quantity
                );
                System.out.println("Food item added successfully!");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        // Release table if the customer added no items
        if (order.getItems().isEmpty()) {

            orderService.cancelOrder(order.getId());

            System.out.println(
                "Empty order cancelled. Table released."
            );

            return;
        }

        System.out.printf(
            "Order total: Rs. %.2f%n",
            orderService.getOrderTotal(order.getId())
        );
    }

    // Display all orders
    private void viewOrders() {

        System.out.println("\n===== ALL ORDERS =====");

        if (orderService.getAllOrders().isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        for (Order order : orderService.getAllOrders()) {

            System.out.printf(
                "Order ID: %d | Table ID: %d"
                + " | Total: Rs. %.2f | Status: %s%n",
                order.getId(),
                order.getTableId(),
                order.calculateTotal(),
                order.getStatus()
            );
        }
    }

    // Process simulated payment
    private void makePayment() {

        System.out.println("Payments are simulated; no real money is processed.");
        int orderId = readInt("Enter order ID: ");

        System.out.println("\nSelect Payment Method");
        System.out.println("1. Cash");
        System.out.println("2. UPI");
        System.out.println("3. Card");

        int choice = readInt("Enter payment choice: ");

        Payment.PaymentMethod method;

        switch (choice) {

            case 1:
                method = Payment.PaymentMethod.CASH;
                break;

            case 2:
                method = Payment.PaymentMethod.UPI;
                break;

            case 3:
                method = Payment.PaymentMethod.CARD;
                break;

            default:
                System.out.println("Invalid payment method!");
                return;
        }

        Payment payment =
                paymentService.makePayment(orderId, method);

        System.out.println("\n========== PAYMENT RECEIPT ==========");
        System.out.println("Payment ID : " + payment.getId());
        System.out.println("Order ID   : " + payment.getOrderId());

        System.out.printf(
            "Amount     : Rs. %.2f%n",
            payment.getAmount()
        );

        System.out.println("Method     : " + payment.getMethod());
        System.out.println("Status     : " + payment.getStatus());
        System.out.println("Time       : " + payment.getCreatedAt());
        System.out.println("======================================");
    }

    // Cancel an unpaid order
    private void cancelOrder() {

        int orderId = readInt("Enter order ID to cancel: ");

        orderService.cancelOrder(orderId);

        System.out.println(
            "Order cancelled successfully. Table released."
        );
    }

    // Complete a paid or ready order
    private void completeOrder() {

        int orderId =
                readInt("Enter order ID to complete: ");

        orderService.markCompleted(orderId);

        System.out.println(
            "Order completed successfully. Table is available."
        );
    }
}
