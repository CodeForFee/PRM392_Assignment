package com.example.assignment.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.DrinkApp;
import com.example.assignment.R;
import com.example.assignment.data.api.ApiClient;
import com.example.assignment.data.api.ApiService;
import com.example.assignment.data.model.CartItem;
import com.example.assignment.data.model.Product;
import com.example.assignment.ui.custom.TopBar;
import com.example.assignment.ui.product.ProductDetailFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TopBar topBar;
    private TextView tvGreeting;
    private ImageView btnCartHome;
    private EditText etSearch;
    private RecyclerView rvCategory, rvProduct;
    private CategoryAdapter catAdapter;
    private ProductAdapter prodAdapter;
    private List<CatItem> categories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private Integer selectedCatId = -1;
    private com.example.assignment.ui.product.ProductDetailFragment.OnCartClick cartListener;

    public void setOnCartClick(com.example.assignment.ui.product.ProductDetailFragment.OnCartClick l) { this.cartListener = l; }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topBar = view.findViewById(R.id.topBar);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        btnCartHome = view.findViewById(R.id.btnCartHome);
        etSearch = view.findViewById(R.id.etSearch);
        rvCategory = view.findViewById(R.id.rvCategory);
        rvProduct = view.findViewById(R.id.rvProduct);

        String userName = DrinkApp.instance.getSessionManager().getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvGreeting.setText("Good to see you, " + userName + "!");
        } else {
            tvGreeting.setText("Good to see you!");
        }

        btnCartHome.setOnClickListener(v -> {
            if (cartListener != null) cartListener.onClick();
        });

        // Topbar might be hidden but let's keep it for compatibility
        if (topBar != null) {
            topBar.setTitle("Drink App");
            topBar.setOnCartClick(v -> {
                if (cartListener != null) cartListener.onClick();
            });
        }
        updateCartBadge();

        // Categories
        rvCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        catAdapter = new CategoryAdapter(categories, id -> {
            selectedCatId = id;
            catAdapter.setSelected(id);
            loadProducts();
        });
        rvCategory.setAdapter(catAdapter);

        // Products grid
        rvProduct.setLayoutManager(new GridLayoutManager(getContext(), 2));
        prodAdapter = new ProductAdapter(products, product -> {
            navigateToDetail(product);
        });
        rvProduct.setAdapter(prodAdapter);

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) { loadProducts(); }
            public void afterTextChanged(Editable s) {}
        });

        loadCategories();
        loadProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        if (topBar != null) {
            topBar.setCartCount(DrinkApp.instance.getCartManager().getItemCount());
        }
    }

    private void navigateToDetail(Product product) {
        ProductDetailFragment frag = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putInt("productId", product.id);
        args.putString("productName", product.name);
        args.putDouble("productPrice", product.price);
        args.putString("productImage", product.imageUrl != null ? product.imageUrl : "");
        frag.setArguments(args);
        frag.setOnCartClick(cartListener);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .hide(this)
                .add(R.id.mainContainer, frag)
                .addToBackStack(null)
                .commit();
    }

    private void loadCategories() {
        ApiClient.getService().getCategories().enqueue(new Callback<List<com.example.assignment.data.model.Category>>() {
            public void onResponse(Call<List<com.example.assignment.data.model.Category>> call, Response<List<com.example.assignment.data.model.Category>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    categories.clear();
                    CatItem all = new CatItem();
                    all.id = -1;
                    all.name = "All";
                    categories.add(all);
                    for (com.example.assignment.data.model.Category c : resp.body()) {
                        CatItem item = new CatItem();
                        item.id = c.id;
                        item.name = c.name;
                        item.description = c.description;
                        categories.add(item);
                    }
                    catAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }
            public void onFailure(Call<List<com.example.assignment.data.model.Category>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: loading categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        String q = etSearch.getText().toString().trim();
        Integer catId = (selectedCatId == null || selectedCatId == -1) ? null : selectedCatId;
        ApiClient.getService().getProducts(catId, q.isEmpty() ? null : q)
                .enqueue(new Callback<List<Product>>() {
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            products.clear();
                            products.addAll(resp.body());
                            prodAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Failed to filter products", Toast.LENGTH_SHORT).show();
                        }
                    }
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(getContext(), "Network error: filtering products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Category Adapter ─────────────────────────────────────────────────────

    static class CatItem {
        public int id = -1;
        public String name = "All";
        public String description;
    }

    static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        private final List<CatItem> list;
        private final OnCatClick listener;
        private int selected = -1;

        interface OnCatClick { void onClick(int id); }

        CategoryAdapter(List<CatItem> list, OnCatClick l) {
            this.list = list; this.listener = l;
        }

        void setSelected(int id) {
            selected = id;
            notifyDataSetChanged();
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int vt) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            CatItem c = list.get(pos);
            h.tv.setText(c.name);
            boolean isSelected = c.id == selected;
            h.tv.setBackground(isSelected ? h.itemView.getContext().getDrawable(R.drawable.bg_chip_selected) : h.itemView.getContext().getDrawable(R.drawable.bg_chip_unselected));
            h.tv.setTextColor(h.itemView.getContext().getColor(isSelected ? R.color.white : R.color.text_primary));
            
            h.itemView.setOnClickListener(v -> {
                int currentPos = h.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    listener.onClick(list.get(currentPos).id);
                }
            });
        }
        @Override public int getItemCount() { return list.size(); }
        static class VH extends RecyclerView.ViewHolder { TextView tv; VH(View v) { super(v); tv = v.findViewById(R.id.tvName); } }
    }

    // ── Product Adapter ──────────────────────────────────────────────────────

    static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
        private final List<Product> list;
        private final OnProdClick listener;

        interface OnProdClick { void onClick(Product product); }

        ProductAdapter(List<Product> list, OnProdClick l) { this.list = list; this.listener = l; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int vt) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Product p = list.get(pos);
            h.tvName.setText(p.name);
            h.tvPrice.setText(String.format("%,.0f VND", p.price));
            if (p.imageUrl != null && !p.imageUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(h.itemView.getContext())
                    .load(p.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(h.iv);
            } else {
                h.iv.setImageResource(R.drawable.ic_launcher_foreground);
            }
            h.itemView.setOnClickListener(v -> listener.onClick(p));
            
        }

        @Override public int getItemCount() { return list.size(); }
        static class VH extends RecyclerView.ViewHolder {
            ImageView iv; TextView tvName, tvPrice;
            VH(View v) { 
                super(v); 
                iv = v.findViewById(R.id.ivProduct); 
                tvName = v.findViewById(R.id.tvName); 
                tvPrice = v.findViewById(R.id.tvPrice); 
            }
        }
    }
}
