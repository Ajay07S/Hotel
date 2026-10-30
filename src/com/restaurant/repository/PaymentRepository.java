
package com.restaurant.repository;

import com.restaurant.models.Payment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRepository {

    private final List<Payment> payments = new ArrayList<>();
    private int nextId = 1;

    public Payment save(
            int orderId,
            double amount,
            Payment.PaymentMethod method) {

        Payment payment = new Payment(
            nextId++, orderId, amount, method
        );

        payments.add(payment);
        return payment;
    }

    public List<Payment> findAll() {
        return new ArrayList<>(payments);
    }

    public Optional<Payment> findById(int id) {
        return payments.stream()
            .filter(payment -> payment.getId() == id)
            .findFirst();
    }

    public Optional<Payment> findByOrderId(int orderId) {
        return payments.stream()
            .filter(payment -> payment.getOrderId() == orderId)
            .findFirst();
    }
}
