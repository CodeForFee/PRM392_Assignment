package com.example.assignment.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.example.assignment.R;

public class BottomNav extends LinearLayout {

    private ImageView ivHome, ivOrders, ivProfile;
    private TextView tvHome, tvOrders, tvProfile;
    private OnNavSelected listener;
    private int selected = 0;

    public interface OnNavSelected {
        void onHome();
        void onOrders();
        void onProfile();
    }

    public BottomNav(Context context) { super(context); init(context); }
    public BottomNav(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(context); }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_bottom_nav, this, true);
        setOrientation(HORIZONTAL);
        setBackgroundColor(getResources().getColor(R.color.bottom_nav_bg, null));
        setPadding(0, dp(8), 0, dp(8));

        ivHome = findViewById(R.id.ivHome);
        ivOrders = findViewById(R.id.ivOrders);
        ivProfile = findViewById(R.id.ivProfile);
        tvHome = findViewById(R.id.tvHome);
        tvOrders = findViewById(R.id.tvOrders);
        tvProfile = findViewById(R.id.tvProfile);

        findViewById(R.id.tabHome).setOnClickListener(v -> select(0));
        findViewById(R.id.tabOrders).setOnClickListener(v -> select(1));
        findViewById(R.id.tabProfile).setOnClickListener(v -> select(2));

        updateUI();
    }

    public void setOnNavSelected(OnNavSelected l) { this.listener = l; }

    private void select(int pos) {
        selected = pos;
        updateUI();
        if (listener != null) {
            if (pos == 0) listener.onHome();
            else if (pos == 1) listener.onOrders();
            else listener.onProfile();
        }
    }

    public void setSelected(int pos) { select(pos); }

    private void updateUI() {
        selectTab(ivHome, tvHome, selected == 0);
        selectTab(ivOrders, tvOrders, selected == 1);
        selectTab(ivProfile, tvProfile, selected == 2);
    }

    private void selectTab(ImageView iv, TextView tv, boolean active) {
        int color = active ? getResources().getColor(R.color.primary, null)
                : getResources().getColor(R.color.text_secondary, null);
        iv.setColorFilter(color);
        tv.setTextColor(color);
        tv.setTextSize(11);
    }

    private int dp(int px) {
        return (int) (px * getResources().getDisplayMetrics().density);
    }
}
