// ============================================================
// datastructure/ProductArray.java
// Data Structure 1 : Array  (Object[][])
//
// Holds the initial raw product seed data.
// Simple, fixed-size, easy to write literal data.
// Each row: { productId, name, category, price, quantity }
// ============================================================

package datastructure;

public class ProductArray {

    // ---- Static seed data (Array) ----
    private final Object[][] data;

    public ProductArray() {
        data = new Object[][] {
            {101, "Samsung Galaxy S23",      "Electronics",  79999.00,  15},
            {102, "Apple AirPods Pro",       "Electronics",  24999.00,  30},
            {103, "Sony WH-1000XM5",         "Electronics",  29999.00,  20},
            {104, "Levi's Men's Jeans",      "Clothing",      2499.00,  50},
            {105, "Nike Air Max Sneakers",   "Footwear",      8999.00,  25},
            {106, "Harry Potter Box Set",    "Books",         1999.00,  40},
            {107, "Prestige Rice Cooker",    "Kitchen",       3499.00,  18},
            {108, "Yoga Mat 6mm",            "Sports",         999.00,  60},
            {109, "Asus VivoBook 15",        "Electronics",  55999.00,  10},
            {110, "Boat Bassheads 100",      "Electronics",    799.00, 100},
            {111, "Wildcraft Backpack 45L",  "Bags",          2299.00,  35},
            {112, "Dettol Hand Wash 500ml",  "Health",         199.00, 200},
            {113, "Classmate Notebook 200pg","Stationery",      89.00, 300},
            {114, "Philips LED Bulb 12W",    "Home Decor",    299.00,  80},
            {115, "Cricket Bat SS Ton",      "Sports",        3999.00,  15},
        };
    }

    // ---- Return the raw 2-D array ----
    public Object[][] getData() {
        return data;
    }

    // ---- Convenience: number of products in the array ----
    public int size() {
        return data.length;
    }
}
