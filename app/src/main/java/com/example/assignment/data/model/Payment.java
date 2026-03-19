package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;

public class Payment {
    public int id;
    @SerializedName("orderId") public int orderId;
    @SerializedName("paymentCode") public String paymentCode;
    public double amount;
    public String status;
    @SerializedName("qrUrl") public String qrUrl;
    @SerializedName("sepayTransactionId") public String sepayTransactionId;
    @SerializedName("paidAt") public String paidAt;
    @SerializedName("createdAt") public String createdAt;
}
