package com.example.assignment.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.ui.custom.TopBar;
import java.util.ArrayList;
import java.util.List;

public class OrderSuccessFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TopBar topBar = view.findViewById(R.id.topBar);
        TextView tvOrderId = view.findViewById(R.id.tvOrderId);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        TextView tvTotal = view.findViewById(R.id.tvTotal);
        Button btnHome = view.findViewById(R.id.btnHome);
        Button btnOrders = view.findViewById(R.id.btnOrders);
        RecyclerView rvImages = view.findViewById(R.id.rvImages);

        topBar.setTitle("Order Success");
        topBar.hideBack();
        topBar.setCartCount(0);

        Bundle args = getArguments();
        if (args != null) {
            tvOrderId.setText("Order ID #" + args.getInt("orderId"));
            tvName.setText(args.getString("customerName", ""));
            tvPhone.setText(args.getString("phone", ""));
            tvTotal.setText(String.format("%,.0f VND", args.getDouble("totalAmount", 0)));

            List<String> imageUrls = args.getStringArrayList("imageUrls");
            if (imageUrls != null && !imageUrls.isEmpty()) {
                rvImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                rvImages.setAdapter(new HorizontalImageAdapter(imageUrls));
            } else {
                rvImages.setVisibility(View.GONE);
            }
        }

        btnHome.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        btnOrders.setOnClickListener(v -> {
            int orderId = -1;
            if (getArguments() != null) orderId = getArguments().getInt("orderId", -1);
            
            requireActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            // Navigate to order detail - handled by MainActivity
            if (getActivity() instanceof OnOrderPlaced) ((OnOrderPlaced) getActivity()).goToOrders(orderId);
        });
    }

    public interface OnOrderPlaced { void goToOrders(int orderId); }
}
