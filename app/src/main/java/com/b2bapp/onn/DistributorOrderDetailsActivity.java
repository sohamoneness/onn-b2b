package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.DistributorOrderItemsAdapter;
import com.b2bapp.onn.adapter.ItemAdapter;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.DistributorOrderModel;
import com.b2bapp.onn.model.ItemModel;
import com.b2bapp.onn.model.OrderModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DistributorOrderDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    public static DistributorOrderModel orderModel;
    private ListView itemList;

    public static ArrayList<ItemModel> itemModelArrayList = new ArrayList<ItemModel>();
    DistributorOrderItemsAdapter itemAdapter;

    private TextView tvTotalQty, tvTotalAmount;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="DistributorOrderDetailsActivity";
    Preferences pref;

    public static String coming_from = "";
    public static String order_id = "";

    public static String distributor_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_order_details);

        getSupportActionBar().hide();

        Log.d("item length>>",String.valueOf(orderModel.getItemModelArrayList().size()));

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        itemList = (ListView) findViewById(R.id.itemList);

        itemAdapter = new DistributorOrderItemsAdapter(DistributorOrderDetailsActivity.this,orderModel.getItemModelArrayList());
        itemList.setAdapter(itemAdapter);

        tvTotalQty = (TextView) findViewById(R.id.tvTotalQty);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);

        int q = 0;
        int a = 0;
        for (int i=0;i<orderModel.getItemModelArrayList().size();i++){
            q+= Integer.parseInt(orderModel.getItemModelArrayList().get(i).getQty());
            int a1 = Integer.parseInt(orderModel.getItemModelArrayList().get(i).getQty())*Integer.parseInt(orderModel.getItemModelArrayList().get(i).getOffer_price());
            a+= a1;
        }

        tvTotalQty.setText(String.valueOf(q));
        //tvTotalAmount.setText("₹"+String.valueOf(a));
        tvTotalAmount.setText("₹0");

        //getOrderDetailsTask();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (coming_from.equals("distributor_order_list")){
            Intent intent = new Intent(DistributorOrderDetailsActivity.this, DistributorOrderListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            AseDistributorOrderListActivity.userId = distributor_id;
            Intent intent = new Intent(DistributorOrderDetailsActivity.this, AseDistributorOrderListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    public void getOrderDetailsTask(){
        dialogView.showCustomSpinProgress(DistributorOrderDetailsActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        Log.d("url>>",WebServices.URL_DISTRIBUTOR_ORDER_DETAILS+"/"+order_id);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_DISTRIBUTOR_ORDER_DETAILS+"/"+order_id, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONObject orderObj = response.getJSONArray("data").getJSONObject(0);

//                                tvStoreName.setText(orderObj.getJSONObject("stores").getString("store_name"));
//                                tvStoreCode.setText("#"+orderObj.getJSONObject("stores").getString("store_OCC_number"));
//                                tvSalesPerson.setText(orderObj.getJSONObject("users").getString("fname")+" "+orderObj.getJSONObject("users").getString("lname"));
//                                tvOrderNo.setText("#"+orderObj.getString("order_no"));
//                                String oldstring = orderObj.getString("created_at");
//                                try {
//                                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oldstring);
//                                    String newstring = new SimpleDateFormat("dd MMM , yyyy").format(date);
//                                    tvDate.setText(newstring);
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//
//                                tvArea.setText(orderObj.getJSONObject("stores").getString("area"));
//                                tvAddress.setText(
//                                        orderObj.getJSONObject("stores").getString("state"));
//                                tvMobile.setText(orderObj.getJSONObject("stores").getString("contact"));
//
                                JSONArray itemsArray = orderObj.getJSONArray("order_products");

                                if (itemsArray.length() > 0) {
                                    for (int i = 0; i < itemsArray.length(); i++) {
                                        JSONObject cartObj = itemsArray.getJSONObject(i);
                                        ItemModel cartModel = new ItemModel();

                                        cartModel.setId(cartObj.getString("id"));
                                        cartModel.setProduct_name(cartObj.getString("product_name"));
                                        cartModel.setProduct_style("");
                                        cartModel.setColor(cartObj.getString("color"));
                                        cartModel.setSize(cartObj.getString("size"));
                                        cartModel.setOffer_price(cartObj.getString("offer_price"));
                                        cartModel.setQty(cartObj.getString("qty"));


                                        itemModelArrayList.add(cartModel);
                                    }
                                }

                                itemAdapter.notifyDataSetChanged();
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