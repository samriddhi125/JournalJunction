package com.example.javaproject;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController extends SignInController{
    @FXML
    private Button backButton;
    @FXML
    private TextField nameBox;
    @FXML
    private TextField emailBox;
    @FXML
    private PasswordField passwordBox;
    @FXML
    private TextField addressBox;
    @FXML
    private TextField numberBox;

//    @FXML
//    private void showError(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Error");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }

    @FXML
    private void backButtonClicked() {
        try {
            // Load the LogIn.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignIn.fxml"));
            Parent root = loader.load();

            // Close the current window
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

            // Show the new scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential errors
            showError("An error occurred while loading the login page.");
        }
    }


    @FXML
    private void saveButtonClicked() {
        String name = nameBox.getText();
        String email = emailBox.getText();
        String password = passwordBox.getText();
        String address = addressBox.getText();
        String phoneNumber = numberBox.getText();
        if (name.isEmpty() || email.isEmpty()|| password.isEmpty()|| address.isEmpty()|| phoneNumber.isEmpty()) {
            showError("Please add all necessary details.");
            return;
        }
        // Database connection and insertion
        try (Connection con = JCrud.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO customersdata (name, email, password, address, phone_number) VALUES (?, ?, ?, ?, ?)")) {

            // Set parameters for the prepared statement
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, hashPassword(password));
            ps.setString(4, address);
            ps.setString(5, phoneNumber);

            // Execute the query
            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("New customer registered successfully!");
                alert.showAndWait();
            } else {
                // Show error message if insertion fails
                showError("Failed to register new customer. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL exceptions
            showError("An error occurred while registering new customer.");
        }
    }


}
