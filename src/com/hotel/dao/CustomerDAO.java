package com.hotel.dao;

import com.hotel.model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO = Data Access Object.
// This class's ONLY job is talking to the customer table.
// No validation here, no UI code - just raw database operations.
public class CustomerDAO {

    // Adds one new customer row. Returns true if it worked.
    public boolean insert(Customer c) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "INSERT INTO customer (full_name, phone, email, nic_passport, nationality, address) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, c.getName());
        ps.setString(2, c.getPhone());
        ps.setString(3, c.getEmail());
        ps.setString(4, c.getNicPassport());
        ps.setString(5, c.getNationality());
        ps.setString(6, c.getAddress());
        return ps.executeUpdate() > 0; // true if a row was actually inserted
    }

    // Updates an existing customer row by ID.
    public boolean update(Customer c) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "UPDATE customer SET full_name=?, phone=?, email=?, nic_passport=?, "
                   + "nationality=?, address=? WHERE customer_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, c.getName());
        ps.setString(2, c.getPhone());
        ps.setString(3, c.getEmail());
        ps.setString(4, c.getNicPassport());
        ps.setString(5, c.getNationality());
        ps.setString(6, c.getAddress());
        ps.setInt(7, c.getId());
        return ps.executeUpdate() > 0;
    }

    // Deletes a customer by ID.
    public boolean delete(int id) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "DELETE FROM customer WHERE customer_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    }

    // Returns every customer, sorted by name.
    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        Connection con = com.hotel.util.DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM customer ORDER BY full_name");
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    // Returns customers whose name/phone/email match the keyword.
    public List<Customer> search(String keyword) throws SQLException {
        List<Customer> list = new ArrayList<>();
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "SELECT * FROM customer WHERE full_name LIKE ? OR phone LIKE ? OR email LIKE ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");
        ps.setString(3, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    // Helper: converts one database row into a Customer object.
    // Used by both findAll() and search() so we don't repeat this code twice.
    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("customer_id"));
        c.setName(rs.getString("full_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setNicPassport(rs.getString("nic_passport"));
        c.setNationality(rs.getString("nationality"));
        c.setAddress(rs.getString("address"));
        return c;
    }
}
