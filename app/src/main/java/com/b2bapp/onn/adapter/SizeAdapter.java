package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.b2bapp.onn.ProductListActivity;
import com.b2bapp.onn.R;
import com.b2bapp.onn.SearchActivity;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.model.SizeModel;

import java.util.ArrayList;

public class SizeAdapter extends BaseAdapter {
    ArrayList<SizeModel> sizeModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;
    EditText etQty;
    int pos;

    public SizeAdapter(Activity activity, ArrayList<SizeModel> sizeModelArrayList) {
        this.mActivity = activity;
        this.context = activity;
        this.sizeModelArrayList = sizeModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return sizeModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inflate_product_size, null, true);

            holder.editText = (EditText) convertView.findViewById(R.id.etQty);
            holder.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            holder.tvSizeDetails = (TextView) convertView.findViewById(R.id.tvSizeDetails);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.editText.setText(sizeModelArrayList.get(position).getQty());
        holder.tvSize.setText(sizeModelArrayList.get(position).getSize());
        holder.tvSizeDetails.setText(sizeModelArrayList.get(position).getDescription());
        //holder.tvSizeDetails.setText("Size : "+sizeModelArrayList.get(position).getDescription());
        //holder.tvPrice.setText(sizeModelArrayList.get(position).getOffer_price());

        if (GlobalVariable.choosed_collection_id.equals("2")){
            holder.tvPrice.setText("No Of Pairs : ");
        }else{
            holder.tvPrice.setText("No Of Pcs : ");
        }

        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sizeModelArrayList.get(position).setQty(holder.editText.getText().toString());
                ProductListActivity.calculateCartAmount();
                SearchActivity.calculateCartAmount();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return convertView;
    }

    private class ViewHolder {

        private EditText editText;
        private TextView tvSize, tvPrice,tvSizeDetails;
    }
}

