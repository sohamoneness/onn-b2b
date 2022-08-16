package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.OrderModel;
import com.b2bapp.onn.model.ProductModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StoreOrderAdapter extends BaseAdapter {
    ArrayList<OrderModel> orderModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public StoreOrderAdapter(Activity activity, ArrayList<OrderModel> orderModelArrayList) {
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
        OrderModel orderModel = orderModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_store_order_list1, parent, false);
        }
        TextView tvStoreName = (TextView) view.findViewById(R.id.tvStoreName);
        //TextView tvStoreCode = (TextView) view.findViewById(R.id.tvStoreCode);
      TextView tvSalesPerson = (TextView) view.findViewById(R.id.tvSalesPerson);
//        TextView tvOrderNo = (TextView) view.findViewById(R.id.tvOrderNo);
//        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvDetails = (TextView) view.findViewById(R.id.tvDetails);
        //tvDetails.setPaintFlags(tvDetails.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvDetails.setText(orderModel.getProduct_count());

        //tvStoreName.setText("Order Id : "+orderModel.getOrder_no());
        tvStoreName.setText(orderModel.getOrder_no());
        //tvStoreCode.setText("Order Value : ₹"+orderModel.getOrder_amount());
        //tvSalesPerson.setText(orderModel.getSales_person());
        //tvOrderNo.setText(orderModel.getOrder_no()+" | ₹"+orderModel.getOrder_amount());

        String oldstring = orderModel.getOrder_date();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oldstring);
            String newstring = new SimpleDateFormat("dd MMM , yyyy").format(date);
            //tvSalesPerson.setText("Order Date : "+newstring);
            tvSalesPerson.setText(newstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }
}

