package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.StoreModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StorelistAdapter extends BaseAdapter {
    ArrayList<StoreModel> storeModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public StorelistAdapter(Activity activity, ArrayList<StoreModel> storeModelArrayList) {
        this.mActivity = activity;
        this.storeModelArrayList = storeModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return storeModelArrayList.size();
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
        StoreModel storeModel = storeModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_store_list, parent, false);
        }
        TextView tvStoreName = (TextView) view.findViewById(R.id.tvStoreName);
        TextView tvStoreCode = (TextView) view.findViewById(R.id.tvStoreCode);
        TextView tvBusinessName = (TextView) view.findViewById(R.id.tvBusinessName);
        TextView tvArea = (TextView) view.findViewById(R.id.tvArea);
        TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);

        tvStoreName.setText(storeModel.getStore_name());
        if (!storeModel.getStore_OCC_number().equals("null") && !storeModel.getStore_OCC_number().equals("")) {
            tvStoreCode.setVisibility(View.VISIBLE);
            tvStoreCode.setText(storeModel.getStore_OCC_number());
        }else{
            tvStoreCode.setVisibility(View.GONE);
        }
        tvBusinessName.setText(storeModel.getBussiness_name());
        tvArea.setText(storeModel.getArea());
        tvAddress.setText(storeModel.getAddress()+", "+storeModel.getArea()+", "+storeModel.getState()+" "+storeModel.getPin());

        return view;
    }

}
