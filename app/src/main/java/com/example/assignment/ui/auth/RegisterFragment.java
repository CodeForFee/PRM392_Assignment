package com.example.assignment.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.assignment.DrinkApp;
import com.example.assignment.R;
import com.example.assignment.data.api.ApiClient;
import com.example.assignment.data.api.ApiService;
import com.example.assignment.data.model.AuthResponse;
import com.example.assignment.data.model.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progress;
    private OnRegisterSuccess listener;

    public interface OnRegisterSuccess { void onRegisterSuccess(); }
    public void setOnRegisterSuccess(OnRegisterSuccess l) { this.listener = l; }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvLogin = view.findViewById(R.id.tvLogin);
        progress = view.findViewById(R.id.progress);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) ((AuthActivity) getActivity()).showLogin();
        });
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Enter your name"); return; }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email address"); return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters"); return;
        }

        setLoading(true);
        ApiService api = ApiClient.getService();
        api.register(new RegisterRequest(name, email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    AuthResponse ar = resp.body();
                    String[] parts = ar.accessToken.split("\\.");
                    if (parts.length >= 2) {
                        try {
                            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
                            org.json.JSONObject jo = new org.json.JSONObject(payload);
                            int userId = jo.optInt("sub", -1);
                            String role = jo.optString("role", "customer");
                            DrinkApp.instance.getSessionManager().saveSession(ar.accessToken, userId, name, email, role);
                            if (listener != null) listener.onRegisterSuccess();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Sign Up Successfully!", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onRegisterSuccess();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Email Already Exists", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}
