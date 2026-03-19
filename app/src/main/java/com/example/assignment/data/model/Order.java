package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Order {
    public int id;
    @SerializedName("userId") public Integer userId;
    @SerializedName("customerName") public String customerName;
    public String phone;
    public String address;
    @SerializedName("totalAmount") public double totalAmount;
    public String status;
    @SerializedName("createdAt") public String createdAt;
    public List<OrderItem> items;
}
