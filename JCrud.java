package com.example.javaproject;

import java.sql.*;
import java.lang.*;
public class JCrud {
    private static final String URL = "jdbc:mysql://localhost:3306/jj";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    // Methods to test the database connection
    private static Connection con;

    public static synchronized Connection getConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return con;
    }
    public static boolean testConnection() {
        try (Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Database connection established successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return false;
        }
    }

    // Getter methods for accessing private variables
    public static String getURL() {
        return URL;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

}