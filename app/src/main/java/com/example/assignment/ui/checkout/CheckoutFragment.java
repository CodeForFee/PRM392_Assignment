package com.example.assignment.ui.checkout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.DrinkApp;
import com.example.assignment.R;
import com.example.assignment.data.api.ApiClient;
import com.example.assignment.data.api.ApiService;
import com.example.assignment.data.local.CartManager;
import com.example.assignment.data.local.SessionManager;
import com.example.assignment.data.model.*;
import com.example.assignment.ui.custom.TopBar;
import com.example.assignment.ui.orders.OrderSuccessFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class CheckoutFragment extends Fragment {

    private TopBar topBar;
    private EditText etName, etPhone, etAddress;
    private TextView tvTotal, tvOrderSummary;
    private RecyclerView rvImages;
    private Button btnPlace;
    private ProgressBar progress;
    private CartManager cartManager;
    private SessionManager sessionManager;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topBar = view.findViewById(R.id.topBar);
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvOrderSummary = view.findViewById(R.id.tvOrderSummary);
        btnPlace = view.findViewById(R.id.btnPlace);
        progress = view.findViewById(R.id.progress);
        rvImages = view.findViewById(R.id.rvImages);

        cartManager = DrinkApp.instance.getCartManager();
        sessionManager = DrinkApp.instance.getSessionManager();

        topBar.setTitle("Checkout");
        topBar.setOnBackClick(v -> requireActivity().getSupportFragmentManager().popBackStack());
        topBar.setCartCount(0);

        // Pre-fill user info
        String userName = sessionManager.getUserName();
        if (!TextUtils.isEmpty(userName)) etName.setText(userName);
        etPhone.setText("");

        // Build summary
        StringBuilder sb = new StringBuilder();
        for (CartItem ci : cartManager.getItems()) {
            sb.append("• ").append(ci.product.name).append(" (").append(ci.size).append(", Ice:").append(ci.iceLevel).append(", Sugar:").append(ci.sugarLevel).append(") x").append(ci.quantity).append("\n");
        }
        tvOrderSummary.setText(sb.toString());
        tvTotal.setText(String.format("%,.0f VND", cartManager.getTotalPrice()));

        // Setup images
        List<String> imageUrls = new ArrayList<>();
        for (CartItem ci : cartManager.getItems()) {
            if (ci.product.imageUrl != null) imageUrls.add(ci.product.imageUrl);
        }
        rvImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(new com.example.assignment.ui.orders.HorizontalImageAdapter(imageUrls));

        btnPlace.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Name required"); return; }
        if (TextUtils.isEmpty(phone) || phone.length() < 9) { etPhone.setError("Invalid phone"); return; }

        setLoading(true);

        // Build order items
        List<OrderItemRequest> orderItems = new ArrayList<>();
        for (CartItem ci : cartManager.getItems()) {
            orderItems.add(new OrderItemRequest(
                    ci.product.id, ci.quantity, ci.size,
                    ci.sugarLevel, ci.iceLevel, ci.toppings
            ));
        }

        int userId = sessionManager.getUserId();
        CreateOrderRequest req = new CreateOrderRequest(name, phone, address, userId > 0 ? userId : null, orderItems);
        ApiService api = ApiClient.getService();
        api.createOrder(req).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    Order order = resp.body();
                    List<String> imageUrls = new ArrayList<>();
                    for (CartItem ci : cartManager.getItems()) {
                        if (ci.product.imageUrl != null) imageUrls.add(ci.product.imageUrl);
                    }
                    cartManager.clearCart();
                    navigateToSuccess(order, imageUrls);
                } else {
                    Toast.makeText(getContext(), "Order failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                setLoading(false);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToSuccess(Order order, List<String> imageUrls) {
        Bundle args = new Bundle();
        args.putInt("orderId", order.id);
        args.putString("customerName", order.customerName);
        args.putString("phone", order.phone);
        args.putDouble("totalAmount", order.totalAmount);

        if (imageUrls != null) {
            args.putStringArrayList("imageUrls", new ArrayList<>(imageUrls));
        }

        OrderSuccessFragment frag = new OrderSuccessFragment();
        frag.setArguments(args);
        requireActivity().getSupportFragmentManager().popBackStack();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.mainContainer, frag)
                .addToBackStack(null)
                .commit();
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPlace.setEnabled(!loading);
        etName.setEnabled(!loading);
        etPhone.setEnabled(!loading);
        etAddress.setEnabled(!loading);
    }
}
