package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.ItemModel;

import java.util.ArrayList;

public class DistributorOrderItemsAdapter extends BaseAdapter {
    ArrayList<ItemModel> itemModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public DistributorOrderItemsAdapter(Activity activity, ArrayList<ItemModel> itemModelArrayList) {
        this.mActivity = activity;
        this.itemModelArrayList = itemModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return itemModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemModel cartModel = itemModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_distributor_item, parent, false);
        }

        TextView tvProductName = (TextView) view.findViewById(R.id.tvProductName);
        TextView tvStyleCode = (TextView) view.findViewById(R.id.tvStyleCode);
        TextView tvColorsize = (TextView) view.findViewById(R.id.tvColorsize);
        //TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);

        tvProductName.setText(cartModel.getProduct_name() + "\n("+cartModel.getColor()+")");
        tvProductName.setSingleLine(false);
        tvStyleCode.setText(cartModel.getSize()+" | "+cartModel.getQty()+"Pcs");
        //tvColorsize.setText("₹"+(Integer.parseInt(cartModel.getOffer_price())*Integer.parseInt(cartModel.getQty())));
        tvColorsize.setText("₹0");

        //tvPrice.setText("₹"+(Integer.parseInt(cartModel.getOffer_price())*Integer.parseInt(cartModel.getQty())));

        return view;
    }
}
