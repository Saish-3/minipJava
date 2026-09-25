// ============================================================
// datastructure/ProductDatabase.java
// Package  : datastructure  (Data Structure Classes)
//
// Manages all products using THREE data structures:
//
//  1. Array (Object[][])
//       - Holds the initial raw product seed data.
//       - Simple, fixed-size, easy to write literal data.
//
//  2. HashMap<Integer, Product>
//       - Key = productId, Value = Product object.
//       - O(1) average-case lookup by product ID.
//       - Used in the "Search by ID" feature.
//
//  3. TreeMap<Integer, Product>
//       - Key = productId (auto-sorted ascending).
//       - O(log n) operations, but always sorted.
//       - Used in "View All Products" for sorted display.
// ============================================================

package datastructure;

import oops.Product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ProductDatabase {

    // ---- HashMap: O(1) search by product ID ----
    private HashMap<Integer, Product> productMap;

    // ---- TreeMap: sorted view by product ID (ascending) ----
    private TreeMap<Integer, Product> sortedProductMap;

    // ---- Constructor: seed data from an array, load into both maps ----
    public ProductDatabase() {
        productMap       = new HashMap<>();
        sortedProductMap = new TreeMap<>();

        // ---- Data Structure 1: Array ----
        // Each row: { productId, name, category, price, quantity }
        Object[][] initialProducts = {
            {101, "Samsung Galaxy S23",     "Electronics",  79999.00,  15},
            {102, "Apple AirPods Pro",      "Electronics",  24999.00,  30},
            {103, "Sony WH-1000XM5",        "Electronics",  29999.00,  20},
            {104, "Levi's Men's Jeans",     "Clothing",      2499.00,  50},
            {105, "Nike Air Max Sneakers",  "Footwear",      8999.00,  25},
            {106, "Harry Potter Box Set",   "Books",         1999.00,  40},
            {107, "Prestige Rice Cooker",   "Kitchen",       3499.00,  18},
            {108, "Yoga Mat 6mm",           "Sports",         999.00,  60},
            {109, "Asus VivoBook 15",       "Electronics",  55999.00,  10},
            {110, "Boat Bassheads 100",     "Electronics",    799.00, 100},
            {111, "Wildcraft Backpack 45L", "Bags",          2299.00,  35},
            {112, "Dettol Hand Wash 500ml", "Health",         199.00, 200},
            {113, "Classmate Notebook 200pg","Stationery",    89.00, 300},
            {114, "Philips LED Bulb 12W",   "Home Decor",    299.00,  80},
            {115, "Cricket Bat SS Ton",     "Sports",        3999.00,  15},
        };

        // Load array data into Data Structures 2 & 3
        for (Object[] row : initialProducts) {
            int    id       = (int)    row[0];
            String name     = (String) row[1];
            String category = (String) row[2];
            double price    = (double) row[3];
            int    qty      = (int)    row[4];

            Product p = new Product(id, name, category, price, qty);

            productMap.put(id, p);       // Data Structure 2: HashMap
            sortedProductMap.put(id, p); // Data Structure 3: TreeMap
        }
    }

    // ---- Search by product ID — uses HashMap (O(1) lookup) ----
    public Product getProductById(int productId) {
        return productMap.get(productId); // returns null if not found
    }

    // ---- Check if a product ID exists ----
    public boolean exists(int productId) {
        return productMap.containsKey(productId);
    }

    // ---- Get all products sorted by ID — uses TreeMap ----
    public Collection<Product> getAllProductsSorted() {
        return sortedProductMap.values();
    }

    // ---- Get total number of products ----
    public int getProductCount() {
        return productMap.size();
    }

    // ---- Keyword search by name or category (case-insensitive partial match) ----
    public List<Product> searchByName(String keyword) {
        List<Product> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Product p : sortedProductMap.values()) {
            if (p.getName().toLowerCase().contains(lower) ||
                p.getCategory().toLowerCase().contains(lower)) {
                results.add(p);
            }
        }
        return results;
    }
}
