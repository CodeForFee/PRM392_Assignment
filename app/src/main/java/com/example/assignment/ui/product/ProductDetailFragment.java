package com.example.assignment.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.assignment.DrinkApp;
import com.example.assignment.R;
import com.example.assignment.data.model.CartItem;
import com.example.assignment.data.model.Product;
import com.example.assignment.ui.custom.TopBar;
import java.util.ArrayList;

public class ProductDetailFragment extends Fragment {

    private TopBar topBar;
    private ImageView ivProduct;
    private TextView tvName, tvPrice;
    private RadioGroup rgIce, rgSugar, rgSize;
    private RadioButton rbIce100, rbSugar100, rbS, rbM, rbL;
    private ImageButton btnMinus, btnPlus;
    private TextView tvQty;
    private Button btnAdd;
    private RelativeLayout btnCartBottom;
    private TextView tvCartBadgeBottom;

    public interface OnCartClick { void onClick(); }
    private OnCartClick cartListener;
    public void setOnCartClick(OnCartClick l) { this.cartListener = l; }

    private Product product;
    private int qty = 1;
    private String selectedStyle = "Ice";
    private String selectedSize = "M";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topBar = view.findViewById(R.id.topBar);
        ivProduct = view.findViewById(R.id.ivProduct);
        tvName = view.findViewById(R.id.tvName);
        tvPrice = view.findViewById(R.id.tvPrice);
        rgIce = view.findViewById(R.id.rgIce);
        rbIce100 = view.findViewById(R.id.rbIce100);
        rgSugar = view.findViewById(R.id.rgSugar);
        rbSugar100 = view.findViewById(R.id.rbSugar100);
        rgSize = view.findViewById(R.id.rgSize);
        rbS = view.findViewById(R.id.rbS);
        rbM = view.findViewById(R.id.rbM);
        rbL = view.findViewById(R.id.rbL);
        btnMinus = view.findViewById(R.id.btnMinus);
        btnPlus = view.findViewById(R.id.btnPlus);
        tvQty = view.findViewById(R.id.tvQty);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnCartBottom = view.findViewById(R.id.btnCartBottom);
        tvCartBadgeBottom = view.findViewById(R.id.tvCartBadgeBottom);

        Bundle args = getArguments();
        if (args != null) {
            product = new Product();
            product.id = args.getInt("productId");
            product.name = args.getString("productName", "");
            product.price = args.getDouble("productPrice", 0);
            product.imageUrl = args.getString("productImage");
        }

        topBar.setTitle("Details");
        topBar.setOnBackClick(v -> requireActivity().getSupportFragmentManager().popBackStack());
        topBar.hideCart();

        // Load image
        if (product.imageUrl != null && !product.imageUrl.isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivProduct);
        }

        tvName.setText(product.name);
        tvPrice.setText(String.format("%,.0f VND", product.price));
        tvQty.setText(String.valueOf(qty));
        updateTotal();

        // Defaults are set in XML (checked:true)

        // Size
        rgSize.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbS) selectedSize = "S";
            else if (checkedId == R.id.rbM) selectedSize = "M";
            else selectedSize = "L";
            updateTotal();
        });

        // Quantity
        btnMinus.setOnClickListener(v -> {
            if (qty > 1) { qty--; tvQty.setText(String.valueOf(qty)); updateTotal(); }
        });
        btnPlus.setOnClickListener(v -> {
            qty++; tvQty.setText(String.valueOf(qty)); updateTotal();
        });

        // Add to cart
        btnAdd.setOnClickListener(v -> {
            addToCart();
        });

        btnCartBottom.setOnClickListener(v -> {
            if (cartListener != null) cartListener.onClick();
        });

        updateCartBadge();
    }

    private void updateCartBadge() {
        int count = DrinkApp.instance.getCartManager().getItemCount();
        if (count > 0) {
            tvCartBadgeBottom.setText(String.valueOf(count));
            tvCartBadgeBottom.setVisibility(View.VISIBLE);
        } else {
            tvCartBadgeBottom.setVisibility(View.GONE);
        }
    }

    private void updateTotal() {
        // No need to update button text with total in the new design if we follow the image closely,
        // but it's helpful. The image just says "Add to My Bag".
        // I'll keep the button text simple as in the image.
    }

    private void addToCart() {
        CartItem item = new CartItem(product);
        item.quantity = qty;
        item.size = selectedSize;
        
        int sugarId = rgSugar.getCheckedRadioButtonId();
        if (sugarId == R.id.rbSugar100) item.sugarLevel = "100%";
        else if (sugarId == R.id.rbSugar70) item.sugarLevel = "70%";
        else if (sugarId == R.id.rbSugar50) item.sugarLevel = "50%";
        else if (sugarId == R.id.rbSugar30) item.sugarLevel = "30%";
        else if (sugarId == R.id.rbSugar0) item.sugarLevel = "0%";

        int iceId = rgIce.getCheckedRadioButtonId();
        if (iceId == R.id.rbIce100) item.iceLevel = "100%";
        else if (iceId == R.id.rbIce70) item.iceLevel = "70%";
        else if (iceId == R.id.rbIce50) item.iceLevel = "50%";
        else if (iceId == R.id.rbIce0) item.iceLevel = "0%";
        
        DrinkApp.instance.getCartManager().addItem(item);
        Toast.makeText(getContext(), "Added to bag!", Toast.LENGTH_SHORT).show();
        updateCartBadge();
        // Don't pop yet, let user see the badge update
    }
}
