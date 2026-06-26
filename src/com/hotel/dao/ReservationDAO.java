package com.hotel.dao;

import com.hotel.model.Reservation;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO for the reservation table. No validation here - that lives
// in ReservationController. This class just runs SQL.
public class ReservationDAO {

    // Inserts a new reservation row. Returns the new reservation's ID.
    public int insert(int customerId, int roomId, Date checkIn, Date checkOut, double total) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "INSERT INTO reservation (customer_id, room_id, check_in_date, check_out_date, total_amount, status) "
                   + "VALUES (?, ?, ?, ?, ?, 'Active')";
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, customerId);
        ps.setInt(2, roomId);
        ps.setDate(3, checkIn);
        ps.setDate(4, checkOut);
        ps.setDouble(5, total);
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1); // the new reservation_id
        }
        return -1;
    }

    // Checks if a room already has an ACTIVE reservation overlapping
    // these dates. Returns true if there's a clash.
    public boolean isRoomBookedForDates(int roomId, Date checkIn, Date checkOut) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM reservation "
                   + "WHERE room_id = ? AND status = 'Active' "
                   + "AND check_in_date < ? AND check_out_date > ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, roomId);
        ps.setDate(2, checkOut);
        ps.setDate(3, checkIn);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt("total") > 0;
    }

    // Looks up the price-per-night for a room (via its category).
    public double getPricePerNight(int roomId) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "SELECT c.price_per_night FROM room r "
                   + "JOIN room_category c ON r.category_id = c.category_id "
                   + "WHERE r.room_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, roomId);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getDouble("price_per_night") : 0.0;
    }

    // Marks a room's status (used right after booking, or after cancelling).
    public void updateRoomStatus(int roomId, String status) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE room SET status=? WHERE room_id=?");
        ps.setString(1, status);
        ps.setInt(2, roomId);
        ps.executeUpdate();
    }

    // Cancels a reservation (sets status to Cancelled).
    public boolean cancel(int reservationId) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "UPDATE reservation SET status='Cancelled' WHERE reservation_id=?");
        ps.setInt(1, reservationId);
        return ps.executeUpdate() > 0;
    }

    // Finds which room a reservation used (needed when cancelling,
    // so we know which room to set back to Available).
    public int findRoomIdByReservationId(int reservationId) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT room_id FROM reservation WHERE reservation_id = ?");
        ps.setInt(1, reservationId);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("room_id") : -1;
    }

    // Returns every reservation, joined with customer name and room number,
    // for display in the table.
    public List<Reservation> findAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "SELECT res.reservation_id, res.customer_id, res.room_id, "
                   + "c.full_name, r.room_number, res.check_in_date, res.check_out_date, "
                   + "res.total_amount, res.status "
                   + "FROM reservation res "
                   + "JOIN customer c ON res.customer_id = c.customer_id "
                   + "JOIN room r ON res.room_id = r.room_id "
                   + "ORDER BY res.reservation_id DESC";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Reservation r = new Reservation();
            r.setId(rs.getInt("reservation_id"));
            r.setCustomerId(rs.getInt("customer_id"));
            r.setRoomId(rs.getInt("room_id"));
            r.setCheckInDate(rs.getDate("check_in_date"));
            r.setCheckOutDate(rs.getDate("check_out_date"));
            r.setTotalAmount(rs.getDouble("total_amount"));
            r.setStatus(rs.getString("status"));
            list.add(r);
        }
        return list;
    }
}
