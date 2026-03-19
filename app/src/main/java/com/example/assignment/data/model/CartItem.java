package com.example.assignment.data.model;

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    public Product product;
    public int quantity = 1;
    public String size = "Regular";
    public String sugarLevel = "100%";
    public String iceLevel = "100%";
    public ArrayList<String> toppings = new ArrayList<>();

    public CartItem(Product product) {
        this.product = product;
    }

    public double getPrice() {
        double base = product.price;
        double sizeExtra = 0;
        if ("Large".equals(size)) sizeExtra = 5000;
        else if ("Small".equals(size)) sizeExtra = -2000;
        
        double toppingExtra = 0;
        for (String t : toppings) toppingExtra += getToppingPrice(t);
        return (base + sizeExtra + toppingExtra) * quantity;
    }

    private double getToppingPrice(String t) {
        if ("Pearl".equals(t) || "Jelly".equals(t) || "Aloe".equals(t)) return 3000;
        if ("Cream".equals(t) || "CreamCheese".equals(t)) return 5000;
        return 0;
    }
}
