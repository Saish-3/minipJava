// ============================================================
// datastructure/ProductDatabase.java
// Package  : datastructure  (Data Structure Classes)
//
// Facade that composes all THREE data-structure classes:
//
//  1. ProductArray   – raw seed data (Object[][])
//  2. ProductHashMap – O(1) lookup by product ID
//  3. ProductTreeMap – always-sorted view by product ID
// ============================================================

package datastructure;

import oops.Product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProductDatabase {

    // ---- Data Structure 1: Array (seed data) ----
    private final ProductArray productArray;

    // ---- Data Structure 2: HashMap – O(1) search by ID ----
    private final ProductHashMap productHashMap;

    // ---- Data Structure 3: TreeMap – sorted view by ID ----
    private final ProductTreeMap productTreeMap;

    // ---- Constructor: seed from array, load into both maps ----
    public ProductDatabase() {
        productArray   = new ProductArray();
        productHashMap = new ProductHashMap();
        productTreeMap = new ProductTreeMap();

        // Iterate the raw array and populate both map structures
        for (Object[] row : productArray.getData()) {
            int    id       = (int)    row[0];
            String name     = (String) row[1];
            String category = (String) row[2];
            double price    = (double) row[3];
            int    qty      = (int)    row[4];

            Product p = new Product(id, name, category, price, qty);

            productHashMap.put(id, p); // Data Structure 2
            productTreeMap.put(id, p); // Data Structure 3
        }
    }

    // ---- Search by product ID — uses HashMap (O(1) lookup) ----
    public Product getProductById(int productId) {
        return productHashMap.get(productId); // returns null if not found
    }

    // ---- Check if a product ID exists ----
    public boolean exists(int productId) {
        return productHashMap.containsKey(productId);
    }

    // ---- Get all products sorted by ID — uses TreeMap ----
    public Collection<Product> getAllProductsSorted() {
        return productTreeMap.values();
    }

    // ---- Get total number of products ----
    public int getProductCount() {
        return productHashMap.size();
    }

    // ---- Keyword search by name or category (case-insensitive partial match) ----
    public List<Product> searchByName(String keyword) {
        List<Product> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Product p : productTreeMap.values()) {
            if (p.getName().toLowerCase().contains(lower) ||
                p.getCategory().toLowerCase().contains(lower)) {
                results.add(p);
            }
        }
        return results;
    }
}
