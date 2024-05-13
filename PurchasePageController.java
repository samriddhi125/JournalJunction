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
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;



public class PurchasePageController {

    @FXML
    private TableView<Product> table_pp;

    @FXML
    private TableColumn<Product, String> product_type;

    @FXML
    private TableColumn<Product, String> description;

    @FXML
    private TableColumn<Product, String> quantity_in_stock;

    @FXML
    private TableColumn<Product, Double> price;
    @FXML
    private TextField search_pp;

    public static String username;
    public int id= getCustomer_id();
    private ObservableList<Product> originalList;

    public PurchasePageController() throws SQLException {
    }

    public void setUsername(String username) {
        this.username = username;
        System.out.println(username);
    }
    @FXML
    private Button checkout_pp;
    @FXML
    void handleCartButtonClicked() {
        try {
            // Get the customer_id
            int id = getCustomer_id();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("AddToCart.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            // Get a reference to the current stage
            Stage currentStage = (Stage) table_pp.getScene().getWindow(); // Replace someNode with any node in the current scene

            // Close the current stage
            currentStage.close();

            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        System.out.println("initialising");
        TableColumn<Product, String> productTypeColumn = new TableColumn<>("Product Type");
        productTypeColumn.setCellValueFactory(new PropertyValueFactory<>("productType"));

        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Product, String> quantityInStockColumn = new TableColumn<>("Quantity In Stock");
        quantityInStockColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Add columns to the table view
        table_pp.getColumns().addAll(productTypeColumn, descriptionColumn, quantityInStockColumn, priceColumn);
        populateTableView(table_pp);

        originalList = FXCollections.observableArrayList();
        originalList.addAll(table_pp.getItems());

        // Add listener to search bar
        search_pp.textProperty().addListener((observable, oldValue, newValue) -> {
            // Call method to filter products based on the search text
            filterProducts(newValue);
        });

    }

    private void filterProducts(String searchText) {
        // Create a filtered list to hold the filtered products
        ObservableList<Product> filteredList = FXCollections.observableArrayList();

        // If the search text is empty, display the original list of products
        if (searchText.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            // Loop through the original list and add products that match the search text
            for (Product product : originalList) {
                // Adjust the condition based on your search criteria
                if (product.getProductType().toLowerCase().contains(searchText.toLowerCase()) ||
                        product.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }

        // Update the table view with the filtered list
        table_pp.setItems(filteredList);
    }



    private void addProduct(Product product) throws SQLException {
        Connection con = JCrud.getConnection();
        String sql = "INSERT INTO Cart (customer_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

       try (
                // Establish JDBC connection
                // Create PreparedStatement
                PreparedStatement preparedStatement = con.prepareStatement(sql);
        ) {
            // Set values for placeholders in the SQL statement
            preparedStatement.setInt(1, getCustomer_id());
            preparedStatement.setString(2, product.getProduct_id());
            preparedStatement.setInt(3, product.getQuantityInStock());
            preparedStatement.setInt(4, product.getPrice());
            
            // Execute the SQL statement
            int rowsAffected = preparedStatement.executeUpdate();
           if (rowsAffected > 0 && product.getQuantityInStock() >= 1) {
               System.out.println("Product added successfully");
           } else if (product.getQuantityInStock() < 0) {
               // Display an "Out of Stock" dialog box
               showErrorDialog("Out of Stock", "The selected product is out of stock.");
           }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getCustomer_id() throws SQLException {
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

    public void printSelectedProduct() throws SQLException {
        Product selectedProduct = table_pp.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            addProduct(selectedProduct);
        }
    }




    public static void populateTableView(TableView<Product> tableView) {
        try {
            // Connect to your MySQL database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jj", "root", "1234");

            // Retrieve data from the database
            ObservableList<Product> products = FXCollections.observableArrayList();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");

            // Populate the TableView with the retrieved data
            while (rs.next()) {
                products.add(new Product(
                        rs.getString("product_type"),
                        rs.getString("description"),
                        rs.getInt("quantity_in_stock"),
                        rs.getInt("price"),
                        rs.getString("product_id")
                ));

                
            }
            tableView.setItems(products);

            // Close resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
