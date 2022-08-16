package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.ProductWiseSalesModel;
import com.b2bapp.onn.model.SecondarySalesModel;

import java.util.ArrayList;

public class ProductWiseSalesAdapter extends BaseAdapter {
    ArrayList<ProductWiseSalesModel> productWiseSalesModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public ProductWiseSalesAdapter(Activity activity, ArrayList<ProductWiseSalesModel> productWiseSalesModelArrayList) {
        this.mActivity = activity;
        this.productWiseSalesModelArrayList = productWiseSalesModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return productWiseSalesModelArrayList.size();
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
        ProductWiseSalesModel productWiseSalesModel = productWiseSalesModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_product_wise_sales, parent, false);
        }

        TextView style_no = (TextView) view.findViewById(R.id.style_no);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView qty = (TextView) view.findViewById(R.id.qty);
        TextView view1 = (TextView) view.findViewById(R.id.view);

        style_no.setText(productWiseSalesModel.getStyle_no());
        name.setText(productWiseSalesModel.getProduct());
        qty.setText(productWiseSalesModel.getQuantity());

        name.setSingleLine(false);

        view1.setPaintFlags(view1.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }
}
