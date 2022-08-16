package com.b2bapp.onn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b2bapp.onn.OrderPreviewActivity;
import com.b2bapp.onn.R;
import com.b2bapp.onn.model.CartModel;

import java.util.List;

public class OrderPreviewAdapter extends RecyclerView.Adapter<OrderPreviewAdapter.Hold> {

    List<CartModel> cList;
    Context context;

    public OrderPreviewAdapter(OrderPreviewActivity orderPreviewActivity, List<CartModel> cartList) {
        this.cList = cartList;
        this.context = orderPreviewActivity;

    }

    @NonNull
    @Override
    public Hold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_preview_row, parent, false);
        return new Hold(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull Hold holder, int position) {

        CartModel cm = cList.get(position);

        holder.tvProduct.setText(cm.getProduct_name());
        holder.tvStyle.setText(cm.getProduct_style());
        holder.tvSize.setText(cm.getSize());
        holder.tvQty.setText(cm.getQty());

    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    public class Hold extends RecyclerView.ViewHolder {

        TextView tvProduct, tvStyle, tvSize, tvQty;

        public Hold(@NonNull View itemView) {
            super(itemView);

            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvStyle = itemView.findViewById(R.id.tvStyle);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvQty = itemView.findViewById(R.id.tvQty);

        }
    }
}
