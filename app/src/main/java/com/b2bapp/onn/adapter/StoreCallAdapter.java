package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.StoreModel;

import java.util.ArrayList;

public class StoreCallAdapter extends BaseAdapter implements View.OnClickListener {
    ArrayList<StoreModel> storeModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private final int CALL_REQUEST = 100;

    public StoreCallAdapter(Activity activity, ArrayList<StoreModel> storeModelArrayList) {
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
            view = inflater.inflate(R.layout.inflate_store_call, parent, false);
        }
        TextView tvStoreName = (TextView) view.findViewById(R.id.tvStoreName);
        TextView tvStoreCode = (TextView) view.findViewById(R.id.tvStoreCode);
        TextView tvBusinessName = (TextView) view.findViewById(R.id.tvBusinessName);
        TextView tvArea = (TextView) view.findViewById(R.id.tvArea);
        TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        ImageView imgCall = (ImageView) view.findViewById(R.id.imgCall);

        tvStoreName.setText(storeModel.getStore_name());
//        if (!storeModel.getStore_OCC_number().equals("null")) {
//            tvStoreCode.setText(storeModel.getStore_OCC_number());
//        }
        if (!storeModel.getStore_OCC_number().equals("null") && !storeModel.getStore_OCC_number().equals("")) {
            tvStoreCode.setVisibility(View.VISIBLE);
            tvStoreCode.setText(storeModel.getStore_OCC_number());
        }else{
            tvStoreCode.setVisibility(View.GONE);
        }
        tvBusinessName.setText(storeModel.getBussiness_name());
        tvArea.setText(storeModel.getArea()+"  |   "+storeModel.getContact());
        tvAddress.setText(storeModel.getAddress()+", "+storeModel.getArea()+", "+storeModel.getState()+" "+storeModel.getPin());

        imgCall.setTag(position);
        imgCall.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        switch (view.getId()){
            case R.id.imgCall:
                callPhoneNumber(storeModelArrayList.get(position).getContact());
                break;
        }
    }

    /**
     * This method is for calling phone no
     * @param phone
     */
    public void callPhoneNumber(String phone) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                    return;
                }
            }

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone));
            mActivity.startActivity(callIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
