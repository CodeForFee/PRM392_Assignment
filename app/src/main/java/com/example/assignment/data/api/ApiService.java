package com.example.assignment.data.api;

import com.example.assignment.data.model.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ── Auth ────────────────────────────────────────────────────────────────
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);


    // ── Categories ──────────────────────────────────────────────────────────
    @GET("categories")
    Call<List<Category>> getCategories();


    // ── Products ────────────────────────────────────────────────────────────
    @GET("products")
    Call<List<Product>> getProducts(@Query("categoryId") Integer categoryId, @Query("q") String q);

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") int id);

    // ── Orders ──────────────────────────────────────────────────────────────
    @POST("orders")
    Call<Order> createOrder(@Body CreateOrderRequest request);

    @GET("orders")
    Call<List<Order>> getOrders(@Query("userId") Integer userId);

    @GET("orders/{id}")
    Call<Order> getOrder(@Path("id") int id);

}
