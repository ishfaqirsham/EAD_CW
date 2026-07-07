package com.hotel.controller;

import com.hotel.dao.PaymentDAO;
import com.hotel.model.Payment;
import java.sql.SQLException;
import java.util.List;

public class PaymentController {

    private PaymentDAO paymentDAO = new PaymentDAO();

    // Validates the amount, checks the customer, then saves the payment.
    // Returns an error message, or null if it succeeded.
    public String recordPayment(int reservationId, double amount) {

        // here the rule: payment amount must be greater than zero
        if (amount <= 0) {
            return "Payment amount must be greater than zero.";
        }

        try {
            int customerId = paymentDAO.findCustomerIdByReservationId(reservationId);
            if (customerId == -1) {
                return "Could not find the customer for this reservation.";
            }

            boolean success = paymentDAO.insert(reservationId, customerId, amount);
            return success ? null : "Failed to record payment.";

        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

    public List<Payment> getAllPayments() {
        try {
            return paymentDAO.findAll();
        } catch (SQLException e) {
            return new java.util.ArrayList<>();
        }
    }
}
