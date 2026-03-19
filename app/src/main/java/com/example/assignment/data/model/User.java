package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    public int id;
    public String name;
    public String email;
    public String role;
    @SerializedName("createdAt") public String createdAt;
    @SerializedName("updatedAt") public String updatedAt;
}
