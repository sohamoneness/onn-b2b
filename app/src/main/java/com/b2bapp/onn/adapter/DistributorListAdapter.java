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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.b2bapp.onn.AseDistributorOrderListActivity;
import com.b2bapp.onn.DistributorActivity;
import com.b2bapp.onn.DistributorMomActivity;
import com.b2bapp.onn.DistributorMomListActivity;
import com.b2bapp.onn.DistributorSearchActivity;
import com.b2bapp.onn.R;
import com.b2bapp.onn.model.DistributorModel;
import com.b2bapp.onn.model.StoreModel;

import java.util.ArrayList;

public class DistributorListAdapter extends BaseAdapter implements View.OnClickListener {
    ArrayList<DistributorModel> distributorModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private final int CALL_REQUEST = 100;

    public DistributorListAdapter(Activity activity, ArrayList<DistributorModel> distributorModelArrayList) {
        this.mActivity = activity;
        this.distributorModelArrayList = distributorModelArrayList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return distributorModelArrayList.size();
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
        DistributorModel distributorModel = distributorModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_distributor, parent, false);
        }
        TextView tvDistributorName = (TextView) view.findViewById(R.id.tvDistributorName);
        TextView tvDistributorCode = (TextView) view.findViewById(R.id.tvDistributorCode);
        TextView tvBusinessName = (TextView) view.findViewById(R.id.tvBusinessName);
        TextView tvArea = (TextView) view.findViewById(R.id.tvArea);
        TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        ImageView imgCall = (ImageView) view.findViewById(R.id.imgCall);
        Button btnAddMom = (Button) view.findViewById(R.id.btnAddMom);
        Button btnOrderList = (Button) view.findViewById(R.id.btnOrderList);
        Button btnMomList = (Button) view.findViewById(R.id.btnMomList);

        tvDistributorName.setText(distributorModel.getStore_name());
        if (!distributorModel.getStore_OCC_number().equals("null")) {
            tvDistributorCode.setText(distributorModel.getStore_OCC_number());
        }
        tvBusinessName.setText(distributorModel.getBussiness_name());
        tvArea.setText(distributorModel.getArea()+"  |   "+distributorModel.getContact());
        tvAddress.setText(distributorModel.getAddress()+", "+distributorModel.getCity()+", "+distributorModel.getState()+" "+distributorModel.getPin());

        imgCall.setTag(position);
        imgCall.setOnClickListener(this);

        btnOrderList.setTag(position);
        btnOrderList.setOnClickListener(this);

        btnAddMom.setTag(position);
        btnAddMom.setOnClickListener(this);

        btnMomList.setTag(position);
        btnMomList.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        switch (view.getId()){
            case R.id.imgCall:
                callPhoneNumber(distributorModelArrayList.get(position).getContact());
                break;
            case R.id.btnOrderList:
                AseDistributorOrderListActivity.coming_from = "distributor_list";
                AseDistributorOrderListActivity.userId = distributorModelArrayList.get(position).getDistributor_id();
                mActivity.startActivity(new Intent(mActivity, AseDistributorOrderListActivity.class));
                mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                mActivity.finish();
                break;
            case R.id.btnAddMom:
                DistributorMomActivity.distributor_name = distributorModelArrayList.get(position).getBussiness_name();
                DistributorMomActivity.distributor_address = distributorModelArrayList.get(position).getAddress();
                mActivity.startActivity(new Intent(mActivity, DistributorMomActivity.class));
                mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                mActivity.finish();
                break;
            case R.id.btnMomList:
                mActivity.startActivity(new Intent(mActivity, DistributorMomListActivity.class)
                        .putExtra("Dist_Name", distributorModelArrayList.get(position).getBussiness_name()));
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

