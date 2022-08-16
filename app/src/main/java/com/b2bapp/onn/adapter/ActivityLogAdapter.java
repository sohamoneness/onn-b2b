package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.ActivityLogModel;
import com.b2bapp.onn.model.CartModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityLogAdapter extends BaseAdapter {
    ArrayList<ActivityLogModel> activityLogModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public ActivityLogAdapter(Activity activity, ArrayList<ActivityLogModel> activityLogModelArrayList) {
        this.mActivity = activity;
        this.activityLogModelArrayList = activityLogModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return activityLogModelArrayList.size();
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
        ActivityLogModel activityLogModel = activityLogModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_activity_log, parent, false);
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


        tvTime.setSingleLine(false);
        tvType.setText(activityLogModel.getType());
        tvComment.setText(activityLogModel.getComment());

        return view;
    }
}
