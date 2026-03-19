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
import com.example.assignment.DrinkApp;
import com.example.assignment.R;
import com.example.assignment.data.api.ApiClient;
import com.example.assignment.data.api.ApiService;
import com.example.assignment.data.local.SessionManager;
import com.example.assignment.data.model.Order;
import com.example.assignment.ui.custom.TopBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private TopBar topBar;
    private RecyclerView rvOrders;
    private TextView tvEmpty;
    private ProgressBar progress;
    private List<Order> orders = new ArrayList<>();
    private OrdersAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topBar = view.findViewById(R.id.topBar);
        rvOrders = view.findViewById(R.id.rvOrders);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progress = view.findViewById(R.id.progress);

        topBar.setTitle("Orders");
        topBar.hideBack();
        topBar.hideCart();

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrdersAdapter(orders, order -> {
            OrderDetailFragment frag = new OrderDetailFragment();
            Bundle args = new Bundle();
            args.putInt("orderId", order.id);
            frag.setArguments(args);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .hide(this)
                    .add(R.id.mainContainer, frag)
                    .addToBackStack(null).commit();
        });
        rvOrders.setAdapter(adapter);

        loadOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
        topBar.setCartCount(DrinkApp.instance.getCartManager().getItemCount());
    }

    private void loadOrders() {
        progress.setVisibility(View.VISIBLE);
        int userId = DrinkApp.instance.getSessionManager().getUserId();
        ApiClient.getService().getOrders(userId > 0 ? userId : null).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> resp) {
                progress.setVisibility(View.GONE);
                if (resp.isSuccessful() && resp.body() != null) {
                    orders.clear();
                    orders.addAll(resp.body());
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── Adapter ───────────────────────────────────────────────────────────────

    static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {
        private final List<Order> orders;
        private final OnOrderClick listener;
        interface OnOrderClick { void onClick(Order order); }
        OrdersAdapter(List<Order> orders, OnOrderClick l) { this.orders = orders; this.listener = l; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int vt) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Order o = orders.get(pos);
            h.tvId.setText("#" + o.id);
            h.tvName.setText(o.customerName);
            h.tvTotal.setText(String.format("%,.0f VND", o.totalAmount));
            h.tvDate.setText(o.createdAt != null ? o.createdAt.substring(0, 10) : "");
            h.tvStatus.setText(getStatusText(o.status));
            h.tvStatus.setBackground(getStatusBg(o.status, h.itemView.getContext()));

            // Setup thumbnails
            List<String> images = new ArrayList<>();
            if (o.items != null) {
                for (com.example.assignment.data.model.OrderItem item : o.items) {
                    if (item.productImageUrl != null) images.add(item.productImageUrl);
                }
            }
            if (!images.isEmpty()) {
                h.rvThumbnails.setVisibility(View.VISIBLE);
                h.rvThumbnails.setLayoutManager(new LinearLayoutManager(h.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                h.rvThumbnails.setAdapter(new HorizontalImageAdapter(images));
            } else {
                h.rvThumbnails.setVisibility(View.GONE);
            }

            h.itemView.setOnClickListener(v -> listener.onClick(o));
        }

        private String getStatusText(String s) {
            switch (s) {
                case "PENDING": return "Pending";
                case "PROCESSING": return "Processing";
                case "COMPLETED": return "Completed";
                case "CANCELLED": return "Cancelled";
                default: return s;
            }
        }

        private android.graphics.drawable.Drawable getStatusBg(String s, android.content.Context ctx) {
            android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
            d.setCornerRadius(dp(6, ctx));
            d.setColor(getStatusColor(s, ctx));
            return d;
        }

        private int getStatusColor(String s, android.content.Context ctx) {
            switch (s) {
                case "PENDING": return ctx.getColor(R.color.warning);
                case "PROCESSING": return ctx.getColor(R.color.primary);
                case "COMPLETED": return ctx.getColor(R.color.success);
                case "CANCELLED": return ctx.getColor(R.color.error);
                default: return ctx.getColor(R.color.text_secondary);
            }
        }

        private float dp(int v, android.content.Context ctx) {
            return v * ctx.getResources().getDisplayMetrics().density;
        }

        @Override public int getItemCount() { return orders.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvId, tvName, tvTotal, tvDate, tvStatus;
            RecyclerView rvThumbnails;
            VH(View v) {
                super(v);
                tvId = v.findViewById(R.id.tvOrderId);
                tvName = v.findViewById(R.id.tvName);
                tvTotal = v.findViewById(R.id.tvTotal);
                tvDate = v.findViewById(R.id.tvDate);
                tvStatus = v.findViewById(R.id.tvStatus);
                rvThumbnails = v.findViewById(R.id.rvThumbnails);
            }
        }
    }
}
