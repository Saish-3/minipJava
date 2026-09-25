// ============================================================
// oops/CartItem.java
// Package  : oops  (OOP Model Classes)
// Represents a single item entry inside the shopping cart.
// Links a Product with the quantity the customer wants to buy.
// ============================================================

package oops;

public class CartItem {

    // ---- Fields ----
    private Product product;   // Reference to the associated product
    private int     quantity;  // Quantity chosen by the customer

    // ---- Constructor ----
    public CartItem(Product product, int quantity) {
        this.product  = product;
        this.quantity = quantity;
    }

    // ---- Getters ----
    public Product getProduct()  { return product; }
    public int     getQuantity() { return quantity; }

    // ---- Setter (for updating quantity) ----
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // ---- Calculate subtotal for this cart item ----
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    // ---- toString for display ----
    @Override
    public String toString() {
        return String.format("[%d] %-25s | Qty: %d | Unit: Rs.%.2f | Subtotal: Rs.%.2f",
                product.getProductId(), product.getName(),
                quantity, product.getPrice(), getSubtotal());
    }
}
