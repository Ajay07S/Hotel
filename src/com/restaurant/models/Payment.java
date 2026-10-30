
package com.restaurant.models;

import java.time.LocalDateTime;

public class Payment {

    public enum PaymentMethod {
        CASH,
        UPI,
        CARD
    }

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        REFUNDED
    }

    private final int id;
    private final int orderId;
    private final double amount;
    private final PaymentMethod method;
    private PaymentStatus status;
    private final LocalDateTime createdAt;

    public Payment(int id, int orderId, double amount, PaymentMethod method) {

        if (id <= 0 || orderId <= 0) {
            throw new IllegalArgumentException(
                "Invalid payment ID or order ID"
            );
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                "Amount must be a finite amount greater than zero"
            );
        }

        if (method == null) {
            throw new IllegalArgumentException(
                "Payment method is required"
            );
        }

        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                "Payment status cannot be null"
            );
        }
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
