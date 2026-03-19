package com.example.assignment.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.assignment.DrinkApp;
import com.example.assignment.R;
import com.example.assignment.ui.MainActivity;

public class AuthActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Already logged in → go to main
        if (DrinkApp.instance.getSessionManager().isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_auth);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        loginFragment.setOnLoginSuccess(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        registerFragment.setOnRegisterSuccess(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        showLogin();
    }

    public void showLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.authContainer, loginFragment)
                .commit();
    }

    public void showRegister() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.authContainer, registerFragment)
                .commit();
    }
}
