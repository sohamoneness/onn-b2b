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
import com.b2bapp.onn.model.VpDistributorModel;

import java.util.ArrayList;

public class AsePrimaryReportAdapter extends BaseAdapter {
    ArrayList<AsePrimaryReportModel> asePrimaryReportModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public AsePrimaryReportAdapter(Activity activity, ArrayList<AsePrimaryReportModel> asePrimaryReportModelArrayList) {
        this.mActivity = activity;
        this.asePrimaryReportModelArrayList = asePrimaryReportModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return asePrimaryReportModelArrayList.size();
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
        AsePrimaryReportModel asePrimaryReportModel = asePrimaryReportModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_ase_primary_report_list, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView qty = (TextView) view.findViewById(R.id.qty);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView view1 = (TextView) view.findViewById(R.id.view);

        if (!asePrimaryReportModel.getDistributor_name().equals("null")){
            name.setText(asePrimaryReportModel.getDistributor_name());
        }

        qty.setText(asePrimaryReportModel.getQty());
        if (!asePrimaryReportModel.getAmount().equals("null")){
            price.setText("₹ "+asePrimaryReportModel.getAmount());
        }else{
            price.setText("₹ 0");
        }

        view1.setPaintFlags(view1.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }
}
