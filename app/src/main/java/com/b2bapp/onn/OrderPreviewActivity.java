package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.OrderPreviewAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.db.DbHelper;
import com.b2bapp.onn.model.OrderDbModel;
import com.b2bapp.onn.model.OrderProductModel;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.ConnectionStatus;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OrderPreviewActivity extends AppCompatActivity {

    ImageView imgBack;
    RecyclerView ordPrvRv;
    OrderPreviewAdapter opadapter;
    Button btnPlaceOrder;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="OrderPreviewActivity";
    Preferences pref;

    String userId = "";
    String userType = "";
    public static String comment = "";
    public static String lati = "";
    public static String longi = "";
    public static String location = "";

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_preview);
        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        dbHelper = new DbHelper(OrderPreviewActivity.this);

        imgBack = findViewById(R.id.imgBack);
        ordPrvRv = findViewById(R.id.ordPrvRv);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        userType = pref.getStringPreference(OrderPreviewActivity.this,Preferences.User_User_type);
        //userId = pref.getStringPreference(OrderPreviewActivity.this,Preferences.Id);
        if (userType.equals("3")){
            userId = GlobalVariable.ase_id_from_asm_dashboard;
        }else{
            userId = pref.getStringPreference(OrderPreviewActivity.this,Preferences.Id);
        }

        if (GlobalVariable.cartList.size()>0){

            opadapter = new OrderPreviewAdapter(OrderPreviewActivity.this, GlobalVariable.cartList);
            ordPrvRv.setLayoutManager(new LinearLayoutManager(OrderPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
            ordPrvRv.setAdapter(opadapter);

        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalVariable.cartList.size()!=0){
//                    if (!etComment.getText().toString().equals("")){
//                        //Calling placeOrderTask method
//                        placeOrderTask();
//                    }else{
//                        CToast.show(ReviewOrderActivity.this,"Please add a comment with your order");
//                    }
                    if (ConnectionStatus.checkConnectionStatus(OrderPreviewActivity.this)){
                        placeOrderTask();
                    }else{
                        OrderDbModel orderDbModel = new OrderDbModel();
                        Random random = new Random();
                        int number = random.nextInt(100000);
                        orderDbModel.setId(String.valueOf(number));
                        orderDbModel.setUser_id(userId);
                        orderDbModel.setStore_id(GlobalVariable.storeModel.getId());
                        orderDbModel.setOrder_type("Store Visit");
                        orderDbModel.setComment(comment);

                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd H:m:s");
                        orderDbModel.setOrder_date(sdf2.format(new Date()));

                        dbHelper.addOrderData(orderDbModel);

                        for (int i=0;i<GlobalVariable.cartList.size();i++){
                            OrderProductModel orderProductModel = new OrderProductModel();
                            Random random1 = new Random();
                            int number1 = random1.nextInt(1000);
                            orderProductModel.setId(String.valueOf(number1));
                            orderProductModel.setProduct_name(GlobalVariable.cartList.get(i).getProduct_name());
                            orderProductModel.setProduct_style(GlobalVariable.cartList.get(i).getProduct_style());
                            orderProductModel.setColor(GlobalVariable.cartList.get(i).getColor());
                            orderProductModel.setSize(GlobalVariable.cartList.get(i).getSize());
                            orderProductModel.setQty(GlobalVariable.cartList.get(i).getQty());
                            orderProductModel.setOffer_price("0");
                            orderProductModel.setOrder_id(String.valueOf(number));
                            dbHelper.addOrderProductData(orderProductModel);
                        }

                        startActivity(new Intent(OrderPreviewActivity.this, OrderPlacedActivity.class));
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }


                }else{
                    CToast.show(OrderPreviewActivity.this,"Please add atleast one product to cart");
                }
            }
        });

    }

    public void placeOrderTask(){
        dialogView.showCustomSpinProgress(OrderPreviewActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("store_id", GlobalVariable.storeModel.getId());
        postParam.put("order_type", GlobalVariable.order_type);
        postParam.put("order_lat", lati);
        postParam.put("order_lng", longi);
        postParam.put("comment", comment);

        Log.d("order params>>",postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_PLACE_ORDER, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                //Calling createActivityLogTask method

                                if (userType.equals("4")){
                                    createActivityLogTask();
                                }else{
                                    startActivity(new Intent(OrderPreviewActivity.this, OrderPlacedActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }

                            }else {
                                dialogView.showSingleButtonDialog(OrderPreviewActivity.this, getResources().getString(R.string.app_name),
                                        response.getString("resp"));
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
    public void createActivityLogTask(){
        String d = "";
        String t = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        d = sdf.format(new Date());

        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        t = sdf1.format(new Date());

        dialogView.showCustomSpinProgress(OrderPreviewActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("date", d);
        postParam.put("time", t);
        postParam.put("type", "Order Upload");
        postParam.put("comment", "Took order from "+GlobalVariable.storeModel.getStore_name());
        postParam.put("lat", lati);
        postParam.put("lng", longi);
        postParam.put("location", location);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_USER_ACTIVITY_CREATE, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {

                                startActivity(new Intent(OrderPreviewActivity.this, OrderPlacedActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(OrderPreviewActivity.this, getResources().getString(R.string.app_name),
                                        response.getString("resp"));
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
}