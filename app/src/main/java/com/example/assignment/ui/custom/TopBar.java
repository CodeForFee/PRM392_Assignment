package com.example.assignment.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.example.assignment.R;

public class TopBar extends LinearLayout {

    private TextView tvTitle;
    private ImageView btnBack;
    private ImageView btnCart;
    private TextView tvCartBadge;

    public TopBar(Context context) { super(context); init(context); }
    public TopBar(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(context); }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_top_bar, this, true);
        setOrientation(HORIZONTAL);
        setGravity(android.view.Gravity.CENTER_VERTICAL);
        setPadding(dp(16), dp(12), dp(16), dp(12));

        tvTitle = findViewById(R.id.tvTitle);
        btnBack = findViewById(R.id.btnBack);
        btnCart = findViewById(R.id.btnCart);
        tvCartBadge = findViewById(R.id.tvCartBadge);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void hideBack() {
        btnBack.setVisibility(GONE);
    }

    public void showBack() {
        btnBack.setVisibility(VISIBLE);
    }

    public void setCartCount(int count) {
        if (count > 0) {
            tvCartBadge.setVisibility(VISIBLE);
            tvCartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(GONE);
        }
    }

    public void hideCart() {
        btnCart.setVisibility(GONE);
        tvCartBadge.setVisibility(GONE);
    }

    public void showCart() {
        btnCart.setVisibility(VISIBLE);
        // Badge visibility will be handled by setCartCount if called later
    }

    public void setOnBackClick(OnClickListener listener) {
        btnBack.setOnClickListener(listener);
    }

    public void setOnCartClick(OnClickListener listener) {
        btnCart.setOnClickListener(listener);
    }

    private int dp(int px) {
        return (int) (px * getResources().getDisplayMetrics().density);
    }
}
