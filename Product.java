package com.example.javaproject;

public class Product {
    private String productType;
    private String description;
    private int quantityInStock;
    private int price;
    private String product_id;

    public Product(String productType, String description, int quantityInStock, int price, String product_id) {
        this.productType = productType;
        this.description = description;
        this.quantityInStock = quantityInStock;
        this.price = price;
        this.product_id = product_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProductType() {
        return productType;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productType='" + productType + '\'' +
                ", description='" + description + '\'' +
                ", quantityInStock='" + quantityInStock + '\'' +
                ", price=" + price +
                '}';
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
