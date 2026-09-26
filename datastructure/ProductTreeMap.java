// ============================================================
// datastructure/ProductTreeMap.java
// Data Structure 3 : TreeMap<Integer, Product>
//
// Key   = productId  (auto-sorted ascending)
// Value = Product object
//
// O(log n) operations, always sorted by product ID.
// Used in "View All Products" (sorted display) and
// keyword search (iterates in ID order).
// ============================================================

package datastructure;

import oops.Product;

import java.util.Collection;
import java.util.TreeMap;

public class ProductTreeMap {

    private final TreeMap<Integer, Product> map;

    public ProductTreeMap() {
        map = new TreeMap<>();
    }

    // ---- Insert / overwrite a product ----
    public void put(int productId, Product product) {
        map.put(productId, product);
    }

    // ---- All products in ascending ID order ----
    public Collection<Product> values() {
        return map.values();
    }

    // ---- Number of products stored ----
    public int size() {
        return map.size();
    }
}
