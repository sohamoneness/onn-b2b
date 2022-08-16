package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.AseModel;
import com.b2bapp.onn.model.AsmModel;

import java.util.ArrayList;

public class AseAdapter extends BaseAdapter {
    ArrayList<AseModel> rsmModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public AseAdapter(Activity activity, ArrayList<AseModel> rsmModelArrayList) {
        this.mActivity = activity;
        this.rsmModelArrayList = rsmModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return rsmModelArrayList.size();
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
        AseModel rsmModel = rsmModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_ase_list, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView value = (TextView) view.findViewById(R.id.value);

        name.setText(rsmModel.getName());
        if (!rsmModel.getValue().equals("null")){
            value.setText("₹ "+rsmModel.getValue());
        }else{
            value.setText("₹ 0");
        }

        return view;
    }
}
