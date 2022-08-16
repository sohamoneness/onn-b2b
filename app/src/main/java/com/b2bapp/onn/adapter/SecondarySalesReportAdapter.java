package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.PrimarySalesModel;
import com.b2bapp.onn.model.SecondarySalesModel;

import java.util.ArrayList;

public class SecondarySalesReportAdapter extends BaseAdapter {
    ArrayList<SecondarySalesModel> secondarySalesModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public SecondarySalesReportAdapter(Activity activity, ArrayList<SecondarySalesModel> secondarySalesModelArrayList) {
        this.mActivity = activity;
        this.secondarySalesModelArrayList = secondarySalesModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return secondarySalesModelArrayList.size();
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
        SecondarySalesModel secondarySalesModel = secondarySalesModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_secondary_sales, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView qty = (TextView) view.findViewById(R.id.qty);
        TextView view1 = (TextView) view.findViewById(R.id.view);

        name.setText(secondarySalesModel.getStore_name());
        qty.setText(secondarySalesModel.getQuantity());

        view1.setPaintFlags(view1.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }
}
