package com.example.assignment.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token") public String accessToken;
    @SerializedName("tokenType") public String tokenType;
    @SerializedName("expiresIn") public long expiresIn;
}
