package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.AsePrimaryReportModel;
import com.b2bapp.onn.model.PrimarySalesModel;

import java.util.ArrayList;

public class PrimarySalesReportAdapter extends BaseAdapter {
    ArrayList<PrimarySalesModel> primarySalesModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public PrimarySalesReportAdapter(Activity activity, ArrayList<PrimarySalesModel> primarySalesModelArrayList) {
        this.mActivity = activity;
        this.primarySalesModelArrayList = primarySalesModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return primarySalesModelArrayList.size();
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
        PrimarySalesModel primarySalesModel = primarySalesModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_primary_sales, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView view1 = (TextView) view.findViewById(R.id.view);
        TextView quantity= (TextView) view.findViewById(R.id.quantity);

        name.setText(primarySalesModel.getDistributor_name());
        if (!primarySalesModel.getAmount().equals("null")){
            price.setText("₹ "+primarySalesModel.getAmount());
        }else{
            price.setText("₹ 0");
        }

        quantity.setText(primarySalesModel.getQuantity());

        view1.setPaintFlags(view1.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }
}
