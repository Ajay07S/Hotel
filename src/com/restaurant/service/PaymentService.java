
package com.restaurant.service;

import com.restaurant.models.Order;
import com.restaurant.models.Payment;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;

public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository) {

        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public Payment makePayment(
            int orderId,
            Payment.PaymentMethod method) {

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() ->
                new IllegalArgumentException("Order not found")
            );

        if (method == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay for a cancelled order");
        }
        if (order.getStatus() == Order.OrderStatus.PAID
                || order.getStatus() == Order.OrderStatus.COMPLETED) {
            throw new IllegalStateException("Order has already been paid");
        }
        if (order.getStatus() != Order.OrderStatus.CONFIRMED
                && order.getStatus() != Order.OrderStatus.PREPARING
                && order.getStatus() != Order.OrderStatus.READY) {
            throw new IllegalStateException(
                "Order must contain items and be confirmed before payment"
            );
        }

        if (order.getItems().isEmpty()) {
            throw new IllegalStateException(
                "Cannot pay for an empty order"
            );
        }

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalStateException(
                "Payment already exists for this order"
            );
        }

        double amount = order.calculateTotal();

        Payment payment = paymentRepository.save(
            orderId, amount, method
        );

        // Payment is simulated; no money is processed.
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        order.setStatus(Order.OrderStatus.PAID);

        return payment;
    }
}
