package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    public int id;
    public String name;
    public double price;
    @SerializedName("imageUrl") public String imageUrl;
    @SerializedName("categoryId") public Integer categoryId;
}
