package app;

import model.entities.Product;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CartSession {

    // productId -> quantity
    private static final Map<Integer, Integer> items = new LinkedHashMap<>();

    private CartSession() {}

    public static void add(Product product, int quantity) {
        if (product == null || product.getId() == null) return;
        if (quantity <= 0) return;

        items.merge(product.getId(), quantity, Integer::sum);
    }

    public static void set(Product product, int quantity) {
        if (product == null || product.getId() == null) return;
        if (quantity <= 0) {
            items.remove(product.getId());
            return;
        }
        items.put(product.getId(), quantity);
    }

    public static void remove(Integer productId) {
        if (productId == null) return;
        items.remove(productId);
    }

    public static void clear() {
        items.clear();
    }

    public static Map<Integer, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public static boolean isEmpty() {
        return items.isEmpty();
    }
}