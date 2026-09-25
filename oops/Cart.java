// ============================================================
// oops/Cart.java
// Package  : oops  (OOP Model Classes)
// Manages the customer's shopping cart.
//
// Data Structure used:
//   LinkedList<CartItem> — maintains insertion order, O(1) add/remove
//
// OOP Concepts demonstrated:
//   - Encapsulation (private fields, public methods)
//   - Association (Cart HAS-A list of CartItems)
// ============================================================

package oops;

import java.util.LinkedList;

public class Cart {

    // ---- LinkedList<CartItem> to store cart items ----
    // LinkedList is used here because:
    //   - Frequent add/remove from any position is O(1)
    //   - We iterate sequentially — no random access needed
    private LinkedList<CartItem> items;

    // ---- Constructor ----
    public Cart() {
        items = new LinkedList<>();
    }

    // ---- Add a product to the cart ----
    // If the product is already in the cart, update its quantity instead.
    public String addItem(Product product, int qty) {
        // Validate requested quantity
        if (qty <= 0) {
            return "Quantity must be greater than zero.";
        }
        if (qty > product.getQuantity()) {
            return "Insufficient stock! Only " + product.getQuantity() + " unit(s) available.";
        }

        // Check if product is already in the cart
        for (CartItem item : items) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                int newQty = item.getQuantity() + qty;
                if (newQty > product.getQuantity() + item.getQuantity()) {
                    return "Insufficient stock!";
                }
                // Restore previous deduction, then deduct new total
                product.restoreStock(item.getQuantity());
                product.reduceStock(newQty);
                item.setQuantity(newQty);
                return "Quantity updated in cart!";
            }
        }

        // New product — deduct stock and add to LinkedList
        product.reduceStock(qty);
        items.add(new CartItem(product, qty));
        return "Product added to cart successfully!";
    }

    // ---- Remove a product from the cart by product ID ----
    public String removeItem(int productId) {
        for (CartItem item : items) {
            if (item.getProduct().getProductId() == productId) {
                item.getProduct().restoreStock(item.getQuantity()); // restore stock
                items.remove(item);
                return "Item removed from cart.";
            }
        }
        return "Product ID " + productId + " not found in cart.";
    }

    // ---- Update quantity of an existing cart item ----
    public String updateQuantity(int productId, int newQty) {
        if (newQty <= 0) {
            return "Quantity must be greater than zero. Use 'Remove' to delete the item.";
        }
        for (CartItem item : items) {
            if (item.getProduct().getProductId() == productId) {
                Product p = item.getProduct();
                int availableTotal = p.getQuantity() + item.getQuantity();
                if (newQty > availableTotal) {
                    return "Only " + availableTotal + " unit(s) available in total.";
                }
                p.restoreStock(item.getQuantity());
                p.reduceStock(newQty);
                item.setQuantity(newQty);
                return "Quantity updated to " + newQty + ".";
            }
        }
        return "Product ID " + productId + " not found in cart.";
    }

    // ---- Calculate grand total of all cart items ----
    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    // ---- Get sum of all quantities across all items ----
    public int getTotalQuantity() {
        int qty = 0;
        for (CartItem item : items) {
            qty += item.getQuantity();
        }
        return qty;
    }

    // ---- Check if cart is empty ----
    public boolean isEmpty() { return items.isEmpty(); }

    // ---- Get number of distinct products in cart ----
    public int getItemCount() { return items.size(); }

    // ---- Get all cart items (for table display) ----
    public LinkedList<CartItem> getItems() { return items; }

    // ---- Clear cart and restore all product stock ----
    public void clearCart() {
        for (CartItem item : items) {
            item.getProduct().restoreStock(item.getQuantity());
        }
        items.clear();
    }

    // ---- Checkout clear: remove items WITHOUT restoring stock (items are sold) ----
    public void checkoutClear() {
        items.clear();
    }
}
