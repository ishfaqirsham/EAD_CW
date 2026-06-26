package com.hotel.dao;

import com.hotel.model.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // Finds which customer is linked to a reservation
    // (needed because the payment table stores customer_id too).
    public int findCustomerIdByReservationId(int reservationId) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT customer_id FROM reservation WHERE reservation_id = ?");
        ps.setInt(1, reservationId);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("customer_id") : -1;
    }

    // Inserts a new payment row.
    public boolean insert(int reservationId, int customerId, double amount) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO payment (reservation_id, customer_id, amount) VALUES (?, ?, ?)");
        ps.setInt(1, reservationId);
        ps.setInt(2, customerId);
        ps.setDouble(3, amount);
        return ps.executeUpdate() > 0;
    }

    // Returns every payment, joined with customer name, newest first.
    public List<Payment> findAll() throws SQLException {
        List<Payment> list = new ArrayList<>();
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "SELECT p.payment_id, p.reservation_id, p.customer_id, p.amount "
                   + "FROM payment p ORDER BY p.payment_id DESC";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Payment p = new Payment();
            p.setPaymentId(rs.getInt("payment_id"));
            p.setReservationId(rs.getInt("reservation_id"));
            p.setCustomerId(rs.getInt("customer_id"));
            p.setAmount(rs.getDouble("amount"));
            list.add(p);
        }
        return list;
    }
}
