package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateOrderRequest {
    @SerializedName("customerName") public String customerName;
    public String phone;
    public String address;
    public Integer userId;
    public List<OrderItemRequest> items;

    public CreateOrderRequest(String customerName, String phone, String address, Integer userId, List<OrderItemRequest> items) {
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.userId = userId;
        this.items = items;
    }
}
