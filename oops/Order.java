// ============================================================
// oops/Order.java
// Package  : oops  (OOP Model Classes)
// Represents a finalized order after successful checkout.
// Captures a snapshot of cart items, coupon, and billing info.
//
// Billing breakdown: Subtotal → Discount → Grand Total
// ============================================================

package oops;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Order {

    // ---- Static counter for auto-incrementing order IDs ----
    private static int orderCounter = 1000;

    // ---- Fields ----
    private int           orderId;
    private Customer      customer;
    private List<CartItem> orderedItems;  // Snapshot of cart at time of checkout
    private int           totalQuantity;
    private String        orderDate;
    private String        status;         // "Confirmed"

    // ---- Discount / Coupon fields ----
    private double subtotal;         // Raw cart total before any discount
    private String couponCode;       // Coupon code applied ("None" if absent)
    private int    discountPercent;  // Discount percentage (0 if no coupon)
    private double discountAmount;   // Rupees discounted
    private double grandTotal;       // Final amount after discount

    // ---- Constructor ----
    // couponCode    : the code entered by the user ("" if none)
    // discountPct   : 0 if no coupon, else e.g. 10 for 10%
    public Order(Customer customer, LinkedList<CartItem> cartItems,
                 double subtotal, int totalQty,
                 String couponCode, int discountPercent) {

        this.orderId        = ++orderCounter;
        this.customer       = customer;
        this.orderedItems   = new ArrayList<>(cartItems); // snapshot copy
        this.totalQuantity  = totalQty;
        this.status         = "Confirmed";

        // ---- Billing calculation ----
        this.subtotal        = subtotal;
        this.couponCode      = (couponCode == null || couponCode.isEmpty()) ? "None" : couponCode.toUpperCase();
        this.discountPercent = discountPercent;
        this.discountAmount  = subtotal * discountPercent / 100.0;
        this.grandTotal      = subtotal - discountAmount;

        // Format current date/time
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.orderDate = LocalDateTime.now().format(fmt);
    }

    // ---- Getters ----
    public int          getOrderId()         { return orderId; }
    public Customer     getCustomer()        { return customer; }
    public List<CartItem> getOrderedItems()  { return orderedItems; }
    public int          getTotalQuantity()   { return totalQuantity; }
    public String       getOrderDate()       { return orderDate; }
    public String       getStatus()          { return status; }
    public double       getSubtotal()        { return subtotal; }
    public String       getCouponCode()      { return couponCode; }
    public int          getDiscountPercent() { return discountPercent; }
    public double       getDiscountAmount()  { return discountAmount; }
    public double       getGrandTotal()      { return grandTotal; }

    // ---- Generate formatted order receipt ----
    // Format: Subtotal → Discount → Grand Total
    public String getOrderSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(55)).append("\n");
        sb.append("         ONLINE SHOPPING CART - ORDER RECEIPT\n");
        sb.append("=".repeat(55)).append("\n");
        sb.append(String.format("  Order ID   : #%d\n", orderId));
        sb.append(String.format("  Order Date : %s\n", orderDate));
        sb.append(String.format("  Status     : %s\n", status));
        sb.append("-".repeat(55)).append("\n");
        sb.append(String.format("  Customer   : %s\n", customer.getName()));
        sb.append(String.format("  Email      : %s\n", customer.getEmail()));
        sb.append(String.format("  Phone      : %s\n", customer.getPhone()));
        sb.append("=".repeat(55)).append("\n");
        sb.append(String.format("  %-3s %-22s %5s %9s %11s\n",
                "ID", "Product", "Qty", "Unit", "Subtotal"));
        sb.append("-".repeat(55)).append("\n");

        for (CartItem item : orderedItems) {
            sb.append(String.format("  %-3d %-22s %5d %9.2f %11.2f\n",
                    item.getProduct().getProductId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.getSubtotal()));
        }

        // ---- Billing summary ----
        sb.append("=".repeat(55)).append("\n");
        sb.append(String.format("  Total Qty      : %-30d\n", totalQuantity));
        sb.append("-".repeat(55)).append("\n");
        sb.append(String.format("  Subtotal       : Rs. %28.2f\n", subtotal));
        if (discountPercent > 0) {
            sb.append(String.format("  Coupon Applied : %-28s\n", couponCode));
            sb.append(String.format("  Discount (%2d%%) : - Rs. %24.2f\n", discountPercent, discountAmount));
        } else {
            sb.append(String.format("  Coupon         : %-28s\n", "No coupon applied"));
        }
        sb.append("-".repeat(55)).append("\n");
        sb.append(String.format("  GRAND TOTAL    : Rs. %28.2f\n", grandTotal));
        sb.append("=".repeat(55)).append("\n");
        sb.append("    Thank you for shopping with us!\n");
        sb.append("=".repeat(55)).append("\n");
        return sb.toString();
    }
}
