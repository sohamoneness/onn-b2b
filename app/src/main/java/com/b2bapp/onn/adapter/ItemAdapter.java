package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.R;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.CartModel;
import com.b2bapp.onn.model.ItemModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemAdapter extends BaseAdapter{
    ArrayList<ItemModel> itemModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    public ItemAdapter(Activity activity, ArrayList<ItemModel> itemModelArrayList) {
        this.mActivity = activity;
        this.itemModelArrayList = itemModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return itemModelArrayList.size();
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
        ItemModel cartModel = itemModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_item1, parent, false);
        }

        TextView tvProductName = (TextView) view.findViewById(R.id.tvProductName);
        TextView tvStyleCode = (TextView) view.findViewById(R.id.tvStyleCode);
        TextView tvColorsize = (TextView) view.findViewById(R.id.tvColorsize);
        //TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);

        tvProductName.setText(cartModel.getProduct_name()+"\n("+cartModel.getColor()+")");
        tvStyleCode.setText(cartModel.getSize());
        tvColorsize.setText(cartModel.getQty()+"Pcs");
        //tvPrice.setText("₹"+(Integer.parseInt(cartModel.getOffer_price())*Integer.parseInt(cartModel.getQty())));

        return view;
    }
}

