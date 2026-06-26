package com.hotel.dao;

import com.hotel.model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Rooms are FIXED at 10 (4 Standard + 3 Deluxe + 3 Suite).
// There is NO insert()/delete() method here on purpose -
// the hotel layout never changes, for any role.
public class RoomDAO {

    private static final String BASE_SELECT =
        "SELECT r.room_id, r.room_number, c.category_name, c.price_per_night, r.status "
      + "FROM room r JOIN room_category c ON r.category_id = c.category_id ";

    // Returns all 10 rooms with category name and price joined in.
    public List<Room> findAll() throws SQLException {
        List<Room> list = new ArrayList<>();
        Connection con = com.hotel.util.DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(BASE_SELECT + "ORDER BY r.room_number");
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    // Updates just the room number.
    public boolean updateRoomNumber(int roomId, String newNumber) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "UPDATE room SET room_number=? WHERE room_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, newNumber);
        ps.setInt(2, roomId);
        return ps.executeUpdate() > 0;
    }

    // Updates just the status (Available/Booked).
    public boolean updateStatus(int roomId, String newStatus) throws SQLException {
        Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "UPDATE room SET status=? WHERE room_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, newStatus);
        ps.setInt(2, roomId);
        return ps.executeUpdate() > 0;
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setCategory(rs.getString("category_name"));
        r.setPricePerNight(rs.getDouble("price_per_night"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}
