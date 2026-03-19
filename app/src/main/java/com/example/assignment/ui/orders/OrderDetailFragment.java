package com.example.assignment.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.data.api.ApiClient;
import com.example.assignment.data.api.ApiService;
import com.example.assignment.data.model.Order;
import com.example.assignment.data.model.OrderItem;
import com.example.assignment.ui.custom.TopBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class OrderDetailFragment extends Fragment {

    private TopBar topBar;
    private TextView tvOrderId, tvName, tvPhone, tvAddress, tvStatus, tvTotal;
    private RecyclerView rvItems;
    private ProgressBar progress;
    private int orderId;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topBar = view.findViewById(R.id.topBar);
        tvOrderId = view.findViewById(R.id.tvOrderId);
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvTotal = view.findViewById(R.id.tvTotal);
        rvItems = view.findViewById(R.id.rvItems);
        progress = view.findViewById(R.id.progress);

        topBar.setTitle("Order Details");
        topBar.setOnBackClick(v -> requireActivity().getSupportFragmentManager().popBackStack());

        if (getArguments() != null) orderId = getArguments().getInt("orderId", -1);
        loadOrder();
    }

    private void loadOrder() {
        progress.setVisibility(View.VISIBLE);
        ApiClient.getService().getOrder(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> resp) {
                progress.setVisibility(View.GONE);
                if (resp.isSuccessful() && resp.body() != null) {
                    bind(resp.body());
                } else {
                    Toast.makeText(getContext(), "Order not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind(Order o) {
        tvOrderId.setText("#" + o.id);
        tvName.setText(o.customerName);
        tvPhone.setText(o.phone);
        tvAddress.setText(o.address != null && !o.address.isEmpty() ? o.address : "N/A");
        tvTotal.setText(String.format("%,.0f VND", o.totalAmount));

        String statusText;
        int statusColor;
        switch (o.status) {
            case "PENDING": statusText = "Pending"; statusColor = getResources().getColor(R.color.warning, null); break;
            case "PROCESSING": statusText = "Processing"; statusColor = getResources().getColor(R.color.primary, null); break;
            case "COMPLETED": statusText = "Completed"; statusColor = getResources().getColor(R.color.success, null); break;
            case "CANCELLED": statusText = "Cancelled"; statusColor = getResources().getColor(R.color.error, null); break;
            default: statusText = o.status; statusColor = getResources().getColor(R.color.text_secondary, null);
        }
        tvStatus.setText(statusText);
        tvStatus.setTextColor(statusColor);

        if (o.items != null && !o.items.isEmpty()) {
            rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
            rvItems.setAdapter(new OrderItemDetailAdapter(o.items));
        }
    }
}
