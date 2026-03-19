package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderItem {
    public Integer id;
    @SerializedName("productId") public int productId;
    @SerializedName("productName") public String productName;
    public int quantity;
    public String size;
    @SerializedName("sugarLevel") public String sugarLevel;
    @SerializedName("iceLevel") public String iceLevel;
    @SerializedName("imageUrl") public String productImageUrl;
    public List<String> toppings;
    public double price;
}
