package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderItemRequest {
    @SerializedName("productId") public int productId;
    public int quantity;
    public String size;
    @SerializedName("sugarLevel") public String sugarLevel;
    @SerializedName("iceLevel") public String iceLevel;
    public List<String> toppings;

    public OrderItemRequest(int productId, int quantity, String size, String sugarLevel, String iceLevel, List<String> toppings) {
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.sugarLevel = sugarLevel;
        this.iceLevel = iceLevel;
        this.toppings = toppings;
    }
}
