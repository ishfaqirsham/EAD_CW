package com.hotel.controller;

import com.hotel.dao.RoomDAO;
import com.hotel.model.Room;
import java.sql.SQLException;
import java.util.List;

public class RoomController {

    private RoomDAO roomDAO = new RoomDAO();

    // Just passes through - no validation needed for a plain list.
    public List<Room> getAllRooms() {
        try {
            return roomDAO.findAll();
        } catch (SQLException e) {
            return new java.util.ArrayList<>();
        }
    }

    // Validates the new room number isn't empty, then updates it.
    public String updateRoomNumber(int roomId, String newNumber) {
        if (newNumber == null || newNumber.trim().isEmpty()) {
            return "Room number cannot be empty.";
        }
        try {
            boolean success = roomDAO.updateRoomNumber(roomId, newNumber);
            return success ? null : "Failed to update room number.";
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

    // Updates the status (Available/Booked). No real validation
    // needed here since the UI uses a dropdown with fixed choices.
    public String updateStatus(int roomId, String newStatus) {
        try {
            boolean success = roomDAO.updateStatus(roomId, newStatus);
            return success ? null : "Failed to update room status.";
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }
}
