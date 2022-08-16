package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.DistributorOrderAdapter;
import com.b2bapp.onn.adapter.StoreOrderAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.LoginActivity;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.DistributorOrderModel;
import com.b2bapp.onn.model.ItemModel;
import com.b2bapp.onn.model.OrderModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DistributorOrderListActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView orderList;
    private ImageView imgBack;

    ArrayList<DistributorOrderModel> orderModelArrayList = new ArrayList<DistributorOrderModel>();
    DistributorOrderAdapter storeOrderAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="DistributorOrderListActivity";
    Preferences pref;

    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_order_list);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        userId = pref.getStringPreference(DistributorOrderListActivity.this,Preferences.Id);

        orderList = (ListView) findViewById(R.id.orderList);
        storeOrderAdapter = new DistributorOrderAdapter(DistributorOrderListActivity.this,orderModelArrayList);
        orderList.setAdapter(storeOrderAdapter);

        //Calling getStoreListTask method
        getStoreListTask();

        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //DistributorOrderDetailsActivity.orderModel = orderModelArrayList.get(i);
                DistributorOrderDetailsActivity.coming_from = "distributor_order_list";
                DistributorOrderDetailsActivity.orderModel = orderModelArrayList.get(i);
                DistributorOrderDetailsActivity.distributor_id = userId;
                startActivity(new Intent(DistributorOrderListActivity.this, DistributorOrderDetailsActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
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
        Intent intent = new Intent(DistributorOrderListActivity.this, DistributorDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for getting order list
     */
    public void getStoreListTask(){
        dialogView.showCustomSpinProgress(DistributorOrderListActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_DISTRIBUTOR_ORDER_LIST+"/"+userId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->orders>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray ordersArray = response.getJSONArray("data");

                                if (ordersArray.length() > 0) {
                                    for (int i = 0; i < ordersArray.length(); i++) {
                                        JSONObject orderObj = ordersArray.getJSONObject(i);
                                        DistributorOrderModel orderModel = new DistributorOrderModel();

                                        orderModel.setId(orderObj.getString("id"));
                                        //orderModel.setStore_name(orderObj.getJSONObject("stores").getString("store_name"));
                                        //orderModel.setStore_code(orderObj.getJSONObject("stores").getString("store_OCC_number"));
                                        orderModel.setSales_person(orderObj.getJSONObject("users").getString("fname")+" "+orderObj.getJSONObject("users").getString("lname"));
                                        orderModel.setOrder_no(orderObj.getString("order_no"));
                                        orderModel.setOrder_amount(orderObj.getString("final_amount"));
                                        orderModel.setOrder_date(orderObj.getString("created_at"));
                                        orderModel.setOrder_status(orderObj.getString("status"));

                                        JSONArray itemsArray = orderObj.getJSONArray("order_products");
                                        ArrayList<ItemModel> itemModelArrayList = new ArrayList<ItemModel>();

                                        if (itemsArray.length() > 0) {
                                            for (int j = 0; j < itemsArray.length(); j++) {
                                                JSONObject cartObj = itemsArray.getJSONObject(j);
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

                                        orderModel.setItemModelArrayList(itemModelArrayList);

                                        orderModelArrayList.add(orderModel);
                                    }
                                }

                                storeOrderAdapter.notifyDataSetChanged();
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