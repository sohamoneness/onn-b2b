package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.ActivityLogModel;
import com.b2bapp.onn.model.StoreVisitModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StoreVisitAdapter extends BaseAdapter {
    ArrayList<StoreVisitModel> storeVisitModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public StoreVisitAdapter(Activity activity, ArrayList<StoreVisitModel> storeVisitModelArrayList) {
        this.mActivity = activity;
        this.storeVisitModelArrayList = storeVisitModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return storeVisitModelArrayList.size();
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
        StoreVisitModel activityLogModel = storeVisitModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_store_visit, parent, false);
        }
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvType = (TextView) view.findViewById(R.id.tvType);
        TextView tvComment = (TextView) view.findViewById(R.id.tvComment);

//        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
//        String currentDateandTime = sdf.format(new Date(activityLogModel.getTime()));

        String oldstring = activityLogModel.getTime();
        try {
            Date date = new SimpleDateFormat("HH:mm:ss").parse(oldstring);
            String newstring = new SimpleDateFormat("h:mm a").format(date);
            tvTime.setText(newstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String oldstring1 = activityLogModel.getDate();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(oldstring1);
            String newstring1 = new SimpleDateFormat("dd MMM, yyyy").format(date);
            tvType.setText(newstring1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvTime.setSingleLine(false);
        tvComment.setText(activityLogModel.getSales_person());

        return view;
    }
}
