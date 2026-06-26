package com.hotel.model;

/**
 * Payment
 * ------------------------------------------------------------
 * MODEL class for one payment record. A payment is linked to a
 * reservation and the customer who made it. One reservation can
 * have more than one payment (for example, a deposit and then a
 * balance payment later).
 */
public class Payment {

    private int paymentId;
    private int reservationId;
    private int customerId;
    private double amount;

    public Payment() {
    }

    public Payment(int paymentId, int reservationId, int customerId, double amount) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.amount = amount;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
