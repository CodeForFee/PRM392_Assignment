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
import com.example.assignment.data.local.SessionManager;
import com.example.assignment.data.model.AuthResponse;
import com.example.assignment.data.model.LoginRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progress;
    private OnLoginSuccess listener;

    public interface OnLoginSuccess { void onLoginSuccess(); }

    public void setOnLoginSuccess(OnLoginSuccess l) { this.listener = l; }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvRegister = view.findViewById(R.id.tvRegister);
        progress = view.findViewById(R.id.progress);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) ((AuthActivity) getActivity()).showRegister();
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ"); return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Mật khẩu tối thiểu 6 ký tự"); return;
        }

        setLoading(true);
        ApiService api = ApiClient.getService();
        api.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    AuthResponse ar = resp.body();
                    // Decode JWT payload (simple base64)
                    String[] parts = ar.accessToken.split("\\.");
                    if (parts.length >= 2) {
                        String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
                        try {
                            org.json.JSONObject jo = new org.json.JSONObject(payload);
                            int userId = jo.optInt("sub", -1);
                            String userName = jo.optString("name", "");
                            String userEmail = jo.optString("email", email);
                            String role = jo.optString("role", "customer");
                            DrinkApp.instance.getSessionManager().saveSession(ar.accessToken, userId, userName, userEmail, role);
                            if (listener != null) listener.onLoginSuccess();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
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
        btnLogin.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
    }
}
