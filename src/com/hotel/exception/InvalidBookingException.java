package com.hotel.exception;

// Our USER-DEFINED (custom) exception, required by the coursework.
// Thrown by ReservationController when a booking breaks a business
// rule: bad dates, room already booked, or missing required fields.
public class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message); // pass the message up to the built-in Exception class
    }
}
