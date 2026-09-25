// ============================================================
// oops/Customer.java
// Package  : oops  (OOP Model Classes)
// Represents a customer in the online shopping system.
// Stores customer ID, name, email, and phone number.
// ============================================================

package oops;

public class Customer {

    // ---- Fields (private — OOP Encapsulation) ----
    private int    customerId;
    private String name;
    private String email;
    private String phone;

    // ---- Constructor ----
    public Customer(int customerId, String name, String email, String phone) {
        this.customerId = customerId;
        this.name       = name;
        this.email      = email;
        this.phone      = phone;
    }

    // ---- Getters ----
    public int    getCustomerId() { return customerId; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public String getPhone()     { return phone; }

    // ---- Setters ----
    public void setName(String name)   { this.name  = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    // ---- toString for display ----
    @Override
    public String toString() {
        return String.format("Customer [%d]: %s | Email: %s | Phone: %s",
                customerId, name, email, phone);
    }
}
