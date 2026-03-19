package com.example.assignment.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.assignment.DrinkApp;
import com.example.assignment.R;
import com.example.assignment.data.local.SessionManager;
import com.example.assignment.ui.auth.AuthActivity;
import com.example.assignment.ui.custom.TopBar;

public class ProfileFragment extends Fragment {

    private TopBar topBar;
    private TextView tvName, tvEmail, tvRole, tvName2, tvEmail2;
    private Button btnLogout;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topBar = view.findViewById(R.id.topBar);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvName2 = view.findViewById(R.id.tvName2);
        tvEmail2 = view.findViewById(R.id.tvEmail2);

        topBar.setTitle("Profile");
        topBar.hideBack();
        topBar.hideCart();

        SessionManager sm = DrinkApp.instance.getSessionManager();
        tvName.setText(sm.getUserName().isEmpty() ? "Guest" : sm.getUserName());
        tvEmail.setText(sm.getUserEmail());
        tvName2.setText(sm.getUserName().isEmpty() ? "Guest" : sm.getUserName());
        tvEmail2.setText(sm.getUserEmail());
        tvRole.setText("customer".equals(sm.getUserRole()) ? "Customer" : "Admin");

        btnLogout.setOnClickListener(v -> {
            sm.clearSession();
            DrinkApp.instance.getCartManager().clearCart();
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), AuthActivity.class));
            getActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
