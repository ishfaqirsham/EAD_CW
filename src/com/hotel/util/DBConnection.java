package com.hotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection
 * ------------------------------------------------------------
 * DESIGN PATTERN USED: SINGLETON
 *
 * "Singleton" means only ONE object of this class is ever
 * created, and every part of the program reuses that same
 * object instead of creating a new one each time.
 *
 * Why: opening a database connection is slow. Instead of opening
 * a brand-new connection every time a screen needs the database,
 * we open it ONCE and every screen asks this class for that same
 * connection.
 *
 * How it works:
 *   1. The constructor is "private" - no other class can write
 *      "new DBConnection()" from outside this file.
 *   2. The only way to get the connection is through the static
 *      method getConnection().
 *   3. getConnection() checks: "do we already have one?" If yes,
 *      it gives that one back. If no, it creates it for the
 *      first time.
 */
public class DBConnection {

    // Holds the single shared instance of this class.
    private static DBConnection instance;

    // Holds the actual database connection.
    private Connection connection;

    // CHANGE THESE 3 VALUES to match your own MySQL / phpMyAdmin setup.
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Private constructor - nobody outside this class can create a new DBConnection.
    private DBConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // This is how every screen gets the ONE shared connection.
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DBConnection(); // created only the first time (or if it was closed)
        }
        return instance.connection;
    }
}
