package com.example.javaproject;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignInController {

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void SignInButtonClicked() {
        String email = emailTextField.getText().trim();
        String password = passwordPasswordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        try {
            boolean isValidUser = SignInMethods.validateUserSignin(email, hashPassword( password));

            if (isValidUser) {
                // User login successful
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PurchasePage.fxml"));
                Parent root = loader.load();

                // Getting the controller for the new scene
                PurchasePageController newController = loader.getController();

                // Passsing the username to the new controller
                newController.setUsername(email);

                // Closing the current window
                Stage currentStage = (Stage) emailTextField.getScene().getWindow();
                currentStage.close();

                // Showing the new scene
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                // Invalid login
                throw new InvalidCredentialsException("Invalid email or password.");
            }
        } catch (InvalidCredentialsException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("An error occurred while processing your request.");
            e.printStackTrace();
        }
    }
    @FXML
    void SignUpButtonClicked() {
        try {
            // Load the SignUp.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = loader.load();

            // Close the current window
            Stage currentStage = (Stage) emailTextField.getScene().getWindow();
            currentStage.close();

            // Show the new scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential errors
            showError("An error occurred while loading the sign-up page.");
        }
    }

    public String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Add password bytes to digest
            md.update(password.getBytes());
            // Get the hashed bytes
            byte[] hashedBytes = md.digest();
            // Convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle exception (e.g., log it, throw a custom exception, etc.)
            return null;
        }
    }
}
