package com.hotel.controller;

import com.hotel.dao.ReservationDAO;
import com.hotel.exception.InvalidBookingException;
import com.hotel.model.Reservation;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

// This is where the custom exception (InvalidBookingException) is
// actually thrown. The View (ReservationForm) calls createReservation()
// inside a try/catch - this Controller does all the checking.
public class ReservationController {

    private ReservationDAO reservationDAO = new ReservationDAO();

    // Creates a new reservation. Returns the total amount charged.
    // Throws InvalidBookingException if any business rule is broken.
    public double createReservation(int customerId, int roomId, Date checkIn, Date checkOut)
            throws InvalidBookingException {

        // Rule 1: dates must exist
        if (checkIn == null || checkOut == null) {
            throw new InvalidBookingException("Both check-in and check-out dates are required.");
        }

        // Rule 2: check-out must be after check-in
        if (!checkOut.after(checkIn)) {
            throw new InvalidBookingException(
                "Check-out date cannot be before or the same as check-in date.");
        }

        try {
            // Rule 3: room can't already be booked for overlapping dates
            if (reservationDAO.isRoomBookedForDates(roomId, checkIn, checkOut)) {
                throw new InvalidBookingException(
                    "This room is already booked for an overlapping date range.");
            }

            // All checks passed - calculate the price
            long millisecondsPerDay = 1000L * 60 * 60 * 24;
            long nights = (checkOut.getTime() - checkIn.getTime()) / millisecondsPerDay;
            double pricePerNight = reservationDAO.getPricePerNight(roomId);
            double total = nights * pricePerNight;

            // Save it, then mark the room as Booked
            reservationDAO.insert(customerId, roomId, checkIn, checkOut, total);
            reservationDAO.updateRoomStatus(roomId, "Booked");

            return total;

        } catch (SQLException e) {
            // Wrap the database error inside our own exception type,
            // so the View only ever needs to catch ONE exception type.
            throw new InvalidBookingException("Database error: " + e.getMessage());
        }
    }

    // Cancels a reservation and frees up its room.
    public String cancelReservation(int reservationId) {
        try {
            int roomId = reservationDAO.findRoomIdByReservationId(reservationId);
            boolean success = reservationDAO.cancel(reservationId);

            if (success && roomId != -1) {
                reservationDAO.updateRoomStatus(roomId, "Available");
            }
            return success ? null : "Failed to cancel reservation.";

        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }
    
         // Marks a reservation Completed (guest checked out normally) and
         // frees up the room, same as cancelling, but a different final status.
         public String completeReservation(int reservationId) {
           try {
               int roomId = reservationDAO.findRoomIdByReservationId(reservationId);
               boolean success = reservationDAO.complete(reservationId);

             if (success && roomId != -1) {
                    reservationDAO.updateRoomStatus(roomId, "Available");
                }
                 return success ? null : "Failed to complete reservation.";

            } catch (SQLException e) {
                 return "Database error: " + e.getMessage();
            }
}

    public List<Reservation> getAllReservations() {
        try {
            return reservationDAO.findAll();
        } catch (SQLException e) {
            return new java.util.ArrayList<>();
        }
    }
}
