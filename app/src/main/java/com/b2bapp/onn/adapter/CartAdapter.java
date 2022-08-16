package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.b2bapp.onn.ProductListActivity;
import com.b2bapp.onn.R;
import com.b2bapp.onn.ReviewOrderActivity;
import com.b2bapp.onn.SplashActivity;
import com.b2bapp.onn.TaskActivity;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.db.DbHelper;
import com.b2bapp.onn.model.CartModel;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.model.UserModel;
import com.b2bapp.onn.utils.ConnectionStatus;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartAdapter extends BaseAdapter implements View.OnClickListener {
    ArrayList<CartModel> cartModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    Activity context;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="CartAdapter";
    Preferences pref;

    String cartId;

    DbHelper dbHelper;

    public CartAdapter(Activity activity, ArrayList<CartModel> cartModelArrayList) {
        this.mActivity = activity;
        this.context = activity;
        this.cartModelArrayList = cartModelArrayList;
        this.inflater = LayoutInflater.from(activity);

        pref=new Preferences(activity);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(activity);

        dbHelper = new DbHelper(mActivity);
    }

    @Override
    public int getCount() {
        return cartModelArrayList.size();
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
        CartModel cartModel = cartModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_cart, parent, false);
        }
        TextView tvProductName = (TextView) view.findViewById(R.id.tvProductName);
        TextView tvStyleCode = (TextView) view.findViewById(R.id.tvStyleCode);
        TextView tvColorsize = (TextView) view.findViewById(R.id.tvColorsize);
        TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        ImageView imgDelete = (ImageView) view.findViewById(R.id.imgDelete);

        tvProductName.setText(cartModel.getProduct_name());
        tvStyleCode.setText(cartModel.getProduct_style());
        tvColorsize.setText(cartModel.getSize()+" - "+cartModel.getQty()+"Pcs");
        tvPrice.setText("₹"+cartModel.getOffer_price());

        imgDelete.setTag(position);
        imgDelete.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        switch (view.getId()){
            case R.id.imgDelete:
                //Calling deleteCartTask method
                //deleteCartTask(position,cartModelArrayList.get(position).getId());
                if (ConnectionStatus.checkConnectionStatus(mActivity)){
                    //Calling deleteCartTask method
                    deleteCartTask(position,cartModelArrayList.get(position).getId());
                }else{
                    dbHelper.deleteParticularCartData(cartModelArrayList.get(position).getId());
                    ((ReviewOrderActivity) context).reloadCart();
                }
                break;
        }
    }

    public void deleteCartTask(int pos,String cartId){
        dialogView.showCustomSpinProgress(mActivity);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_CART_DELETE+"/"+cartId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->all data>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                cartModelArrayList.remove(pos);
                                ((ReviewOrderActivity) context).getCartListTask();
                                //notifyDataSetChanged();
                            }else {
                                dialogView.showSingleButtonDialog(mActivity, mActivity.getResources().getString(R.string.app_name),
                                        response.getString("resp"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                dialogView.dismissCustomSpinProgress();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        mQueue.add(jsonObjReq);
    }
}
