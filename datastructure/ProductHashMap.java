// ============================================================
// datastructure/ProductHashMap.java
// Data Structure 2 : HashMap<Integer, Product>
//
// Key   = productId
// Value = Product object
//
// O(1) average-case lookup by product ID.
// Used in "Search by ID" and existence checks.
// ============================================================

package datastructure;

import oops.Product;

import java.util.Collection;
import java.util.HashMap;

public class ProductHashMap {

    private final HashMap<Integer, Product> map;

    public ProductHashMap() {
        map = new HashMap<>();
    }

    // ---- Insert / overwrite a product ----
    public void put(int productId, Product product) {
        map.put(productId, product);
    }

    // ---- O(1) lookup by product ID ----
    public Product get(int productId) {
        return map.get(productId); // returns null if not found
    }

    // ---- Check existence ----
    public boolean containsKey(int productId) {
        return map.containsKey(productId);
    }

    // ---- Number of products stored ----
    public int size() {
        return map.size();
    }

    // ---- All product values (unordered) ----
    public Collection<Product> values() {
        return map.values();
    }
}
