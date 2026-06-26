package com.hotel.model;

/**
 * Room
 * ------------------------------------------------------------
 * MODEL class for one room. Holds the room's id, number,
 * category (Standard/Deluxe/Suite), price for one night, and
 * its current status ("Available" or "Booked").
 */
public class Room {

    private int id;
    private String roomNumber;
    private String category;
    private double pricePerNight;
    private String status;

    public Room() {
    }

    public Room(int id, String roomNumber, String category, double pricePerNight, String status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Shown automatically when a Room object is placed into a JComboBox.
    @Override
    public String toString() {
        return roomNumber + " - " + category + " - Rs." + pricePerNight;
    }
}
