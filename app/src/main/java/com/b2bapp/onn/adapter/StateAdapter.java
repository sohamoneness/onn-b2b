package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.ItemModel;
import com.b2bapp.onn.model.StateModel;

import java.util.ArrayList;

public class StateAdapter extends BaseAdapter {
    ArrayList<StateModel> stateModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public StateAdapter(Activity activity, ArrayList<StateModel> stateModelArrayList) {
        this.mActivity = activity;
        this.stateModelArrayList = stateModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return stateModelArrayList.size();
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
        StateModel stateModel = stateModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_state_list, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView value = (TextView) view.findViewById(R.id.value);

        name.setText(stateModel.getName());

        if (!stateModel.getValue().equals("null")){
            value.setText("₹ "+stateModel.getValue());
        }else{
            value.setText("₹ 0");
        }


        return view;
    }
}


