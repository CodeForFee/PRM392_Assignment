package com.example.assignment.ui.cart;

import android.os.Bundle;
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
import com.example.assignment.data.local.CartManager;
import com.example.assignment.data.model.CartItem;
import com.example.assignment.ui.checkout.CheckoutFragment;
import com.example.assignment.ui.custom.TopBar;
import java.util.List;

public class CartFragment extends Fragment {

    private TopBar topBar;
    private RecyclerView rvCart;
    private TextView tvOrderHeader, tvTotalItems, tvTotal, tvEmpty;
    private Button btnCheckout;
    private View llBottomBar;
    private CartAdapter adapter;
    private CartManager cartManager;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topBar = view.findViewById(R.id.topBar);
        rvCart = view.findViewById(R.id.rvCart);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvOrderHeader = view.findViewById(R.id.tvOrderHeader);
        tvTotalItems = view.findViewById(R.id.tvTotalItems);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        llBottomBar = view.findViewById(R.id.llBottomBar);

        cartManager = DrinkApp.instance.getCartManager();
        topBar.setTitle("Bag");
        topBar.setOnBackClick(v -> requireActivity().getSupportFragmentManager().popBackStack());
        topBar.setCartCount(cartManager.getItemCount());

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(cartManager.getItems(), this::updateTotal);
        rvCart.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            if (cartManager.getItemCount() == 0) {
                Toast.makeText(getContext(), "Bag is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.mainContainer, new CheckoutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        updateTotal();
    }

    void updateTotal() {
        int count = cartManager.getItemCount();
        tvOrderHeader.setText("Order (" + count + ")");
        tvTotalItems.setText("Total (" + count + " Items)");
        
        double total = cartManager.getTotalPrice();
        tvTotal.setText(String.format("%,.0f VND", total));
        adapter.notifyDataSetChanged();
        topBar.setCartCount(count);
        
        boolean isEmpty = count == 0;
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        llBottomBar.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        rvCart.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvOrderHeader.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvTotalItems.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        btnCheckout.setEnabled(!isEmpty);
    }

    // ── Cart Adapter ─────────────────────────────────────────────────────────

    static class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
        private final List<CartItem> items;
        private final Runnable onChange;
        CartAdapter(List<CartItem> items, Runnable onChange) {
            this.items = items; this.onChange = onChange;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int vt) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            CartItem item = items.get(pos);
            h.tvName.setText(item.product.name);
            String opts = item.size + " · Ice: " + item.iceLevel + " · Sugar: " + item.sugarLevel;
            h.tvOpts.setText(opts);
            h.tvQty.setText(String.valueOf(item.quantity));
            h.tvPrice.setText(String.format("%,.0f VND", item.getPrice()));
            
            h.btnMinus.setOnClickListener(v -> {
                if (item.quantity > 1) {
                    item.quantity--;
                    onChange.run();
                } else {
                    int idx = h.getAdapterPosition();
                    if (idx >= 0) DrinkApp.instance.getCartManager().removeItem(idx);
                    onChange.run();
                }
            });
            h.btnPlus.setOnClickListener(v -> {
                item.quantity++;
                onChange.run();
            });
            
            if (item.product.imageUrl != null && !item.product.imageUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(h.itemView.getContext())
                    .load(item.product.imageUrl)
                    .into(h.iv);
            }
        }
        @Override public int getItemCount() { return items.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvOpts, tvQty, tvPrice;
            ImageButton btnMinus, btnPlus;
            ImageView iv;
            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvName);
                tvOpts = v.findViewById(R.id.tvOpts);
                tvQty = v.findViewById(R.id.tvQtyNum);
                tvPrice = v.findViewById(R.id.tvPrice);
                btnMinus = v.findViewById(R.id.btnMinus);
                btnPlus = v.findViewById(R.id.btnPlus);
                iv = v.findViewById(R.id.ivProduct);
            }
        }
    }
}
