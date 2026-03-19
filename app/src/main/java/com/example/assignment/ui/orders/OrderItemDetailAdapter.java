package com.example.assignment.ui.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.assignment.R;
import com.example.assignment.data.model.OrderItem;
import java.util.List;

public class OrderItemDetailAdapter extends RecyclerView.Adapter<OrderItemDetailAdapter.VH> {

    private final List<OrderItem> items;

    public OrderItemDetailAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OrderItem item = items.get(position);
        holder.tvProductName.setText(item.productName != null ? item.productName : "Product #" + item.productId);
        
        String info = (item.size != null ? item.size : "M") + ", Ice:" + 
                      (item.iceLevel != null ? item.iceLevel : "70%") + ", Sugar:" + 
                      (item.sugarLevel != null ? item.sugarLevel : "100%");
        holder.tvProductInfo.setText(info);
        
        if (item.toppings != null && !item.toppings.isEmpty()) {
            holder.tvToppings.setVisibility(View.VISIBLE);
            holder.tvToppings.setText("+ " + String.join(", ", item.toppings));
        } else {
            holder.tvToppings.setVisibility(View.GONE);
        }
        
        holder.tvQuantity.setText("x" + item.quantity);
        holder.tvPrice.setText(String.format("%,.0f VND", item.price));
        
        Glide.with(holder.itemView.getContext())
                .load(item.productImageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivProduct);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductName, tvProductInfo, tvToppings, tvQuantity, tvPrice;
        VH(View v) {
            super(v);
            ivProduct = v.findViewById(R.id.ivProduct);
            tvProductName = v.findViewById(R.id.tvProductName);
            tvProductInfo = v.findViewById(R.id.tvProductInfo);
            tvToppings = v.findViewById(R.id.tvToppings);
            tvQuantity = v.findViewById(R.id.tvQuantity);
            tvPrice = v.findViewById(R.id.tvPrice);
        }
    }
}
