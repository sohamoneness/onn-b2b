package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.AseSalesModel;
import com.b2bapp.onn.model.SecondarySalesModel;

import java.util.ArrayList;

public class AseSalesReportAdapter extends BaseAdapter {
    ArrayList<AseSalesModel> aseSalesModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public AseSalesReportAdapter(Activity activity, ArrayList<AseSalesModel> aseSalesModelArrayList) {
        this.mActivity = activity;
        this.aseSalesModelArrayList = aseSalesModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return aseSalesModelArrayList.size();
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
        AseSalesModel aseSalesModel = aseSalesModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_secondary_sales, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView qty = (TextView) view.findViewById(R.id.qty);
        TextView view1 = (TextView) view.findViewById(R.id.view);

        name.setText(aseSalesModel.getAse_name());
        qty.setText(aseSalesModel.getQuantity());

        view1.setPaintFlags(view1.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }
}
