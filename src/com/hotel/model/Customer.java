package com.hotel.model;

/**
 * Customer
 * ------------------------------------------------------------
 * MODEL class (the "M" in MVC). Holds the data for one customer.
 * No database code and no UI code lives in this class - this is
 * OOP ENCAPSULATION: fields are private, and the only way to
 * read or change them is through public getter/setter methods.
 */
public class Customer {

    private int id;
    private String name;
    private String phone;
    private String email;
    private String nicPassport;
    private String nationality;
    private String address;

    public Customer() {
    }

    public Customer(int id, String name, String phone, String email,
                     String nicPassport, String nationality, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nicPassport = nicPassport;
        this.nationality = nationality;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNicPassport() {
        return nicPassport;
    }

    public void setNicPassport(String nicPassport) {
        this.nicPassport = nicPassport;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
