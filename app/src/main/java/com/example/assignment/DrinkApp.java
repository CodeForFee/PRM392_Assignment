package com.example.assignment;

import android.app.Application;
import com.example.assignment.data.local.SessionManager;
import com.example.assignment.data.local.CartManager;
import com.example.assignment.data.api.ApiClient;

public class DrinkApp extends Application {

    private SessionManager sessionManager;
    private CartManager cartManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sessionManager = new SessionManager(this);
        cartManager = new CartManager(this);

        // Restore token if exists
        if (sessionManager.isLoggedIn()) {
            ApiClient.setAuthToken(sessionManager.getToken());
        }
    }

    public SessionManager getSessionManager() { return sessionManager; }
    public CartManager getCartManager() { return cartManager; }

    public static DrinkApp instance;
}
