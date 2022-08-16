package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.DistributorOrderModel;
import com.b2bapp.onn.model.OrderModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DistributorOrderAdapter extends BaseAdapter {
    ArrayList<DistributorOrderModel> orderModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public DistributorOrderAdapter(Activity activity, ArrayList<DistributorOrderModel> orderModelArrayList) {
        this.mActivity = activity;
        this.orderModelArrayList = orderModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return orderModelArrayList.size();
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
        DistributorOrderModel orderModel = orderModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_distributor_order_list1, parent, false);
        }
        TextView tvStoreName = (TextView) view.findViewById(R.id.tvStoreName);
        //TextView tvStoreCode = (TextView) view.findViewById(R.id.tvStoreCode);
        TextView tvSalesPerson = (TextView) view.findViewById(R.id.tvSalesPerson);
        TextView tvOrderNo = (TextView) view.findViewById(R.id.tvOrderNo);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);

        tvStoreName.setText("Order Id : "+orderModel.getOrder_no());
        //tvStoreCode.setText("Order Value : ₹"+orderModel.getOrder_amount());
        tvSalesPerson.setText(orderModel.getSales_person());
        //tvOrderNo.setText(orderModel.getOrder_no()+" | ₹"+orderModel.getOrder_amount());

        String status = orderModel.getOrder_status();
        String order_status = "";

        if (status.equals("1")){
            order_status = "New Order";
        }else if (status.equals("2")){
            order_status = "Order Confirmed";
        }else if (status.equals("3")){
            order_status = "Order Shipped";
        }else if (status.equals("4")){
            order_status = "Order Delivered";
        }else if (status.equals("5x")){
            order_status = "Order Cancelled";
        }

        String oldstring = orderModel.getOrder_date();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oldstring);
            String newstring = new SimpleDateFormat("dd MMM , yyyy").format(date);
            tvSalesPerson.setText("Order Date : "+newstring+" | "+order_status);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }
}
