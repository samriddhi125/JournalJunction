package com.example.javaproject;

import java.sql.*;

// Functionality for Scene1 SignIn Page
public class SignInMethods {
    // Accessing the static variables from JCrud class
    static String URL = JCrud.getURL();
    static String USERNAME = JCrud.getUSERNAME();
    static String PASSWORD = JCrud.getPASSWORD();

    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static boolean validateUserSignin(String email, String password) {
        String query = "SELECT * FROM customersdata WHERE email = ? AND password = ?";
        try (Connection conn = createConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
