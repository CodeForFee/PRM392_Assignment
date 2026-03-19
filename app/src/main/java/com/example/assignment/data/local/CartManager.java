package com.example.assignment.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.assignment.data.model.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREF = "cart";
    private static final String KEY_CART = "cart_items";
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private ArrayList<CartItem> items = new ArrayList<>();

    public CartManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        load();
    }

    private void load() {
        String json = prefs.getString(KEY_CART, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<CartItem>>(){}.getType();
            items = gson.fromJson(json, type);
        }
    }

    private void save() {
        prefs.edit().putString(KEY_CART, gson.toJson(items)).apply();
    }

    public List<CartItem> getItems() { return items; }

    public void addItem(CartItem item) {
        items.add(item);
        save();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            save();
        }
    }

    public void updateItem(int position, CartItem item) {
        if (position >= 0 && position < items.size()) {
            items.set(position, item);
            save();
        }
    }

    public void clearCart() {
        items.clear();
        save();
    }

    public int getItemCount() { return items.size(); }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) total += item.getPrice();
        return total;
    }
}
