// ============================================================
// oops/Product.java
// Package  : oops  (OOP Model Classes)
// Represents a product in the online shopping system.
// Stores product ID, name, category, price, and available stock.
// ============================================================

package oops;

public class Product {

    // ---- Fields (private — OOP Encapsulation) ----
    private int    productId;
    private String name;
    private String category;
    private double price;
    private int    quantity;  // Available stock quantity

    // ---- Constructor ----
    public Product(int productId, String name, String category, double price, int quantity) {
        this.productId = productId;
        this.name      = name;
        this.category  = category;
        this.price     = price;
        this.quantity  = quantity;
    }

    // ---- Getters ----
    public int    getProductId() { return productId; }
    public String getName()      { return name; }
    public String getCategory()  { return category; }
    public double getPrice()     { return price; }
    public int    getQuantity()  { return quantity; }

    // ---- Setters ----
    public void setName(String name)         { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price)       { this.price = price; }
    public void setQuantity(int quantity)    { this.quantity = quantity; }

    // ---- Reduce stock when a product is added to cart ----
    public void reduceStock(int amount) {
        this.quantity -= amount;
    }

    // ---- Restore stock when a product is removed from cart ----
    public void restoreStock(int amount) {
        this.quantity += amount;
    }

    // ---- toString for display ----
    @Override
    public String toString() {
        return String.format("[%d] %-25s | Category: %-15s | Price: Rs.%-8.2f | Stock: %d",
                productId, name, category, price, quantity);
    }
}
