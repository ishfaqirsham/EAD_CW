package com.hotel.controller;

import com.hotel.dao.CustomerDAO;
import com.hotel.model.Customer;
import java.sql.SQLException;
import java.util.List;

// Controller = sits between the UI (View) and the DAO.
// The UI calls these methods. These methods check the data is
// valid FIRST, then hand off to CustomerDAO to actually touch
// the database. This is where validation rules live - the DAO
// itself does NOT validate anything, it just trusts the data it's given.
public class CustomerController {

    private CustomerDAO customerDAO = new CustomerDAO();

    // Validates the customer, then inserts it.
    // Returns an error message String if validation fails, or null if it succeeded.
    public String addCustomer(Customer c) {

        String error = validate(c);
        if (error != null) return error; // stop here if validation failed

        try {
            boolean success = customerDAO.insert(c);
            return success ? null : "Failed to add customer.";
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

    // Same pattern as addCustomer, but for updating.
    public String updateCustomer(Customer c) {

        String error = validate(c);
        if (error != null) return error;

        try {
            boolean success = customerDAO.update(c);
            return success ? null : "Failed to update customer.";
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

    // Deletes a customer. No validation needed here - just an ID.
    public String deleteCustomer(int id) {
        try {
            boolean success = customerDAO.delete(id);
            return success ? null : "Failed to delete customer.";
        } catch (SQLException e) {
            // This usually means the customer has linked reservations/payments
            return "Could not delete. This customer may have existing reservations or payments.";
        }
    }

    // Just passes through to the DAO - nothing to validate for a simple list.
    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.findAll();
        } catch (SQLException e) {
            return new java.util.ArrayList<>(); // return empty list on error
        }
    }

    public List<Customer> searchCustomers(String keyword) {
        try {
            return customerDAO.search(keyword);
        } catch (SQLException e) {
            return new java.util.ArrayList<>();
        }
    }

    // All the validation rules for a Customer, in one place.
    // Returns null if everything is valid, otherwise returns the
    // first error message found.
    private String validate(Customer c) {
        if (c.getName() == null || c.getName().trim().isEmpty()) {
            return "Customer name cannot be empty.";
        }
        if (c.getPhone() == null || c.getPhone().length() != 10) {
            return "Phone must be exactly 10 digits.";
        }
        if (c.getEmail() == null || !c.getEmail().contains("@") || !c.getEmail().contains(".")) {
            return "Email format is invalid.";
        }
        return null; // all checks passed
    }
}
