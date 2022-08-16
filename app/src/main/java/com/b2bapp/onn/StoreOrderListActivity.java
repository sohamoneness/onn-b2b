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
import com.b2bapp.onn.adapter.StoreOrderAdapter;
import com.b2bapp.onn.adapter.StorelistAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.OrderModel;
import com.b2bapp.onn.model.PrimarySalesModel;
import com.b2bapp.onn.model.SecondarySalesModel;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreOrderListActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView orderList;
    private ImageView imgBack;

    ArrayList<OrderModel> orderModelArrayList = new ArrayList<OrderModel>();
    StoreOrderAdapter storeOrderAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="StoreOrderListActivity";
    Preferences pref;

    private String storeId = "";
    public static String coming_from = "";
    public static StoreModel storeModel;

    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_order_list);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userType = pref.getStringPreference(StoreOrderListActivity.this,Preferences.User_User_type);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        if (coming_from.equals("store_details")) {
            storeId = GlobalVariable.storeModel.getId();
        }else{
            storeId = GlobalVariable.store_id;
        }

        orderList = (ListView) findViewById(R.id.orderList);
        storeOrderAdapter = new StoreOrderAdapter(StoreOrderListActivity.this,orderModelArrayList);
        orderList.setAdapter(storeOrderAdapter);

        //Calling getStoreListTask method
        //getStoreListTask();
        if (coming_from.equals("store_details")){
            getStoreListFilter1();
        }else if (coming_from.equals("dashboard")){
            getStoreListFilter();
        }else if (coming_from.equals("sales_report")){
            getStoreListFilter();
        }else if (coming_from.equals("ase_store_wise_report")){
            getStoreListFilter();
        }else if (coming_from.equals("asm_ase_store_wise_report")){
            getStoreListFilter();
        }

        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StoreOrderDetailsActivity.orderModel = orderModelArrayList.get(i);
                StoreOrderDetailsActivity.coming_from = "store";
                startActivity(new Intent(StoreOrderListActivity.this, StoreOrderDetailsActivity.class));
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
        if (coming_from.equals("store_details")){
            StoreDetailsActivity.storeModel = storeModel;
            Intent intent = new Intent(StoreOrderListActivity.this, StoreDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if (coming_from.equals("dashboard")){
            Intent intent = new Intent(StoreOrderListActivity.this, AseDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if (coming_from.equals("sales_report")){
            Intent intent = new Intent(StoreOrderListActivity.this, AseSalesReportActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if (coming_from.equals("ase_store_wise_report")){
            Intent intent = new Intent(StoreOrderListActivity.this, AseStoreWiseReportResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if (coming_from.equals("asm_ase_store_wise_report")){
            Intent intent = new Intent(StoreOrderListActivity.this, AsmAseWiseTeamReportResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    public void getStoreListFilter(){
        dialogView.showCustomSpinProgress(StoreOrderListActivity.this);

        Map<String, String> postParam= new HashMap<String, String>();
        if(userType.equals("1")){
            postParam.put("store_id", storeId);
            postParam.put("date_from", GlobalVariable.vp_report_date_from);
            postParam.put("date_to", GlobalVariable.vp_report_date_to);
            postParam.put("collection", GlobalVariable.vp_report_collection);
            postParam.put("style_no", GlobalVariable.vp_report_style_no);
        }else if(userType.equals("2")){
            postParam.put("store_id", storeId);
            postParam.put("date_from", GlobalVariable.rsm_report_date_from);
            postParam.put("date_to", GlobalVariable.rsm_report_date_to);
            postParam.put("collection", GlobalVariable.rsm_report_collection);
            postParam.put("style_no", GlobalVariable.rsm_report_style_no);
        }else if(userType.equals("3")){
            postParam.put("store_id", storeId);
            postParam.put("date_from", GlobalVariable.asm_report_date_from);
            postParam.put("date_to", GlobalVariable.asm_report_date_to);
            postParam.put("collection", GlobalVariable.asm_report_collection);
            postParam.put("style_no", GlobalVariable.asm_report_style_no);
        }else if(userType.equals("4")){
            postParam.put("store_id", storeId);
            postParam.put("date_from", GlobalVariable.store_report_date_from);
            postParam.put("date_to", GlobalVariable.store_report_date_to);
            postParam.put("collection", GlobalVariable.store_report_collection);
            postParam.put("style_no", GlobalVariable.store_report_style_no);
        }

        Log.d("store_id>>",storeId);
        Log.d("date_from>>",GlobalVariable.store_report_date_from);
        Log.d("date_to>>",GlobalVariable.store_report_date_to);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_ORDER_FILTER, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray ordersArray = response.getJSONArray("data");

                                if (ordersArray.length() > 0) {
                                    for (int i = 0; i < ordersArray.length(); i++) {
                                        JSONObject orderObj = ordersArray.getJSONObject(i);
                                        OrderModel orderModel = new OrderModel();

                                        orderModel.setId(orderObj.getString("id"));
                                        orderModel.setStore_name(orderObj.getJSONObject("stores").getString("store_name"));
                                        orderModel.setStore_code(orderObj.getJSONObject("stores").getString("store_OCC_number"));
                                        orderModel.setSales_person(orderObj.getJSONObject("users").getString("fname")+" "+orderObj.getJSONObject("users").getString("lname"));
                                        orderModel.setOrder_no(orderObj.getString("order_no"));
                                        orderModel.setOrder_amount(orderObj.getString("final_amount"));
                                        orderModel.setOrder_date(orderObj.getString("created_at"));
                                        orderModel.setProduct_count(orderObj.getString("product_count"));

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
                Log.e(TAG, "Error: " + error.getMessage());
                if(error.equals("invalid_credentials")){
                    Log.e("invalid>>","You have entered a wrong credentials");
                }
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

    public void getStoreListFilter1(){
        dialogView.showCustomSpinProgress(StoreOrderListActivity.this);

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("store_id", storeId);

        Log.d("store_id>>",storeId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_ORDER_FILTER, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray ordersArray = response.getJSONArray("data");

                                if (ordersArray.length() > 0) {
                                    for (int i = 0; i < ordersArray.length(); i++) {
                                        JSONObject orderObj = ordersArray.getJSONObject(i);
                                        OrderModel orderModel = new OrderModel();

                                        orderModel.setId(orderObj.getString("id"));
                                        orderModel.setStore_name(orderObj.getJSONObject("stores").getString("store_name"));
                                        orderModel.setStore_code(orderObj.getJSONObject("stores").getString("store_OCC_number"));
                                        orderModel.setSales_person(orderObj.getJSONObject("users").getString("fname")+" "+orderObj.getJSONObject("users").getString("lname"));
                                        orderModel.setOrder_no(orderObj.getString("order_no"));
                                        orderModel.setOrder_amount(orderObj.getString("final_amount"));
                                        orderModel.setOrder_date(orderObj.getString("created_at"));
                                        orderModel.setProduct_count(orderObj.getString("product_count"));

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
                Log.e(TAG, "Error: " + error.getMessage());
                if(error.equals("invalid_credentials")){
                    Log.e("invalid>>","You have entered a wrong credentials");
                }
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

    /**
     * This method is for getting order list
     */
    public void getStoreListTask(){
        dialogView.showCustomSpinProgress(StoreOrderListActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_STORE_WISE_ORDER_LIST+"/"+storeId, new JSONObject(postParam),
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
                                        OrderModel orderModel = new OrderModel();

                                        orderModel.setId(orderObj.getString("id"));
                                        orderModel.setStore_name(orderObj.getJSONObject("stores").getString("store_name"));
                                        orderModel.setStore_code(orderObj.getJSONObject("stores").getString("store_OCC_number"));
                                        orderModel.setSales_person(orderObj.getJSONObject("users").getString("fname")+" "+orderObj.getJSONObject("users").getString("lname"));
                                        orderModel.setOrder_no(orderObj.getString("order_no"));
                                        orderModel.setOrder_amount(orderObj.getString("final_amount"));
                                        orderModel.setOrder_date(orderObj.getString("created_at"));

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