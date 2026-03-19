package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;

public class CreatePaymentRequest {
    @SerializedName("orderId") public int orderId;
    public CreatePaymentRequest(int orderId) {
        this.orderId = orderId;
    }
}
