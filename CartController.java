package com.example.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.javaproject.PurchasePageController.username;


public class CartController implements Initializable {
    @FXML
    private TableView<Product> table_pp;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    @FXML
    private Label sumLabel;
    @FXML
    private Button checkout_pp;

    private int customer_id;

    private ObservableList<Product> fetchedProducts = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<Product, String> productTypeColumn = new TableColumn<>("Product Type");
        productTypeColumn.setCellValueFactory(new PropertyValueFactory<>("productType"));

        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Product, String> quantityInStockColumn = new TableColumn<>("Quantity In Stock");
        quantityInStockColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        try {
            customer_id = getCustomer_id(PurchasePageController.username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Add columns to the table view
        table_pp.getColumns().addAll(productTypeColumn, descriptionColumn, quantityInStockColumn, priceColumn);

        // Call method to populate table view
        try {
            List<String> product_ids = loadProducts();
            fetchProducts(product_ids, table_pp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCustomer_id(String username) throws SQLException {
        Connection con = JCrud.getConnection();
        String sql = "SELECT customer_id FROM Customersdata WHERE email = ?";

        try (
                // Create PreparedStatement
                PreparedStatement preparedStatement = con.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, username);
            // Set values for placeholders in the SQL statement
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Check if result set has any rows
                if (resultSet.next()) {
                    // Retrieve customer_id from the result set
                    return resultSet.getInt("customer_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<String> loadProducts() throws SQLException {
        List<String> product_ids = new ArrayList<>();
        Connection con = JCrud.getConnection();
        String sql = "SELECT * FROM cart WHERE customer_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customer_id);
            // Retrieve data from the database
            ObservableList<Product> products = FXCollections.observableArrayList();
            ResultSet rs = ps.executeQuery();
            // Populate the TableView with the retrieved data
            while (rs.next()) {
                product_ids.add(rs.getString("product_id"));
            }
            System.out.println(products);
            // Close resources
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } System.out.println("Product ids: " + product_ids);
        return product_ids;
    }

    public void fetchProducts(List<String> product_ids, TableView<Product> tableView) {
        // Create an ObservableList to hold the fetched products
        fetchedProducts.clear();

        // SQL query
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection con = JCrud.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Iterate through each product_id in the list
            for (String product_id : product_ids) {
                // Set the product_id parameter in the PreparedStatement
                ps.setString(1, product_id);

                // Execute the query
                try (ResultSet rs = ps.executeQuery()) {
                    // Process the result set and add products to the list
                    while (rs.next()) {
                        fetchedProducts.add(new Product(
                                rs.getString("product_type"),
                                rs.getString("description"),
                                rs.getInt("quantity_in_stock"),
                                rs.getInt("price"),
                                rs.getString("product_id")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int sum=0;
        for (Product product : fetchedProducts) {
            sum+=product.getPrice();
        } sumLabel.setText(String.valueOf(sum));
//        tableView.setItems(null);
        // Set the fetched products as items of the TableView
        tableView.setItems(fetchedProducts);
    }

    @FXML
    private void deleteProduct() {
        System.out.println("deleteProduct");
        Product selectedProduct = table_pp.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            System.out.println("Selected Product: " + selectedProduct);
            deleteSelectedProduct(selectedProduct.getProduct_id());
            try {
                table_pp.setItems(null);
                fetchedProducts.remove(selectedProduct);
                List<String> product_ids = loadProducts();
                fetchProducts(product_ids, table_pp);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteSelectedProduct(String product_id) {
        String sql = "DELETE FROM cart WHERE product_id = ? LIMIT 1";

        try (Connection con = JCrud.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Set the product_id parameter in the PreparedStatement
            ps.setString(1, product_id);

            // Execute the delete query
            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Product deleted successfully");

            } else {
                System.out.println("Failed to delete product");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToMainPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("PurchasePage.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

// Get a reference to the current stage
        Stage currentStage = (Stage) backButton.getScene().getWindow(); // Replace someNode with any node in the current scene

// Close the current stage
        currentStage.close();

// Create a new stage and set the scene
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void removeFromStock() {
        // Check if the cart is empty
        if (fetchedProducts.isEmpty()) {
            // Show a dialog or message to the user indicating the cart is empty
            Dialog<String> emptyCartDialog = new Dialog<>();
            emptyCartDialog.setTitle("Checkout");
            emptyCartDialog.setContentText("Your cart is empty. Please add items to your cart before checking out.");

            // Add an OK button
            ButtonType buttonTypeOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            emptyCartDialog.getDialogPane().getButtonTypes().add(buttonTypeOK);

            // Show the dialog and wait for user response
            emptyCartDialog.showAndWait();

            // Do not proceed further since the cart is empty
            return;
        }

        // If the cart is not empty, proceed with the checkout process
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Checkout");
        dialog.setContentText("Your order has been placed, it will be delivered to your address soon!");

        // Add buttons
        ButtonType buttonTypeOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOK);

        // Show the dialog and wait for user response
        dialog.showAndWait();

        // Update product stock in the database and remove products from the cart
        String sql = "UPDATE products SET quantity_in_stock = quantity_in_stock - 1 WHERE product_id = ?";

        try (Connection connection = JCrud.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Product product : fetchedProducts) {
                statement.setString(1, product.getProduct_id());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        // Delete products from the cart and clear the cart
        for (Product product : fetchedProducts) {
            deleteSelectedProduct(product.getProduct_id());
        }

        // Clear the fetchedProducts list
        fetchedProducts.clear();

        // Reload products and refresh the cart
        try {
            List<String> product_ids = loadProducts();
            fetchProducts(product_ids, table_pp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

