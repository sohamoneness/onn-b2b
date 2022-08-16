package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.b2bapp.onn.adapter.CartAdapter;
import com.b2bapp.onn.adapter.DistributorCartAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.CartModel;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DistributorReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private TextView tvStoreName, tvStoreCode, tvArea, tvBusinessName, tvProductName, tvProductCount;
    private EditText etComment;
    private Button btnPlaceOrder,btnContinueShopping;

    private ListView cartList;

    ArrayList<CartModel> cartModelArrayList = new ArrayList<CartModel>();
    DistributorCartAdapter cartAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="SplashActivity";
    Preferences pref;

    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_review);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(DistributorReviewActivity.this,Preferences.Id);

        imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(this);

        tvStoreName = (TextView) findViewById(R.id.tvStoreName);
        tvStoreCode = (TextView) findViewById(R.id.tvStoreCode);
        tvArea = (TextView) findViewById(R.id.tvArea);
        tvBusinessName = (TextView) findViewById(R.id.tvBusinessName);
        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvProductCount = (TextView) findViewById(R.id.tvProductCount);

//        tvStoreName.setText(GlobalVariable.storeModel.getStore_name());
//        tvStoreCode.setText("#"+GlobalVariable.storeModel.getStore_OCC_number());
//        tvArea.setText(GlobalVariable.storeModel.getArea());
//        tvBusinessName.setText(GlobalVariable.storeModel.getBussiness_name());
        tvProductName.setText(GlobalVariable.productModel.getName());

        etComment = (EditText) findViewById(R.id.etComment);

        cartList = (ListView) findViewById(R.id.cartList);

        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(this);
        btnContinueShopping = (Button) findViewById(R.id.btnContinueShopping);
        btnContinueShopping.setOnClickListener(this);

        cartAdapter = new DistributorCartAdapter(DistributorReviewActivity.this, cartModelArrayList);
        cartList.setAdapter(cartAdapter);

        //Calling getCartListTask method
        getCartListTask();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.btnPlaceOrder:
                //Checking cart model is blank or not
                if (cartModelArrayList.size()!=0){
//                    if (!etComment.getText().toString().equals("")){
//                        //Calling placeOrderTask method
//                        placeOrderTask();
//                    }else{
//                        CToast.show(DistributorReviewActivity.this,"Please add a comment with your order");
//                    }
                    placeOrderTask();

                }else{
                    CToast.show(DistributorReviewActivity.this,"Please add atleast one product to cart");
                }
                break;
            case R.id.btnContinueShopping:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DistributorReviewActivity.this, DistributorProductsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for getting cart list
     */
    public void getCartListTask(){
        dialogView.showCustomSpinProgress(DistributorReviewActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_DISTRIBUTOR_CART_LIST+"/"+userId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray cartsArray = response.getJSONArray("data");

                                if (cartsArray.length() > 0) {
                                    for (int i = 0; i < cartsArray.length(); i++) {
                                        JSONObject cartObj = cartsArray.getJSONObject(i);
                                        CartModel cartModel = new CartModel();

                                        cartModel.setId(cartObj.getString("id"));
                                        cartModel.setProduct_name(cartObj.getString("product_name"));
                                        cartModel.setProduct_style(cartObj.getString("product_style_no"));
                                        cartModel.setColor(cartObj.getString("color"));
                                        cartModel.setSize(cartObj.getString("size"));
                                        cartModel.setOffer_price(cartObj.getString("offer_price"));
                                        cartModel.setQty(cartObj.getString("qty"));


                                        cartModelArrayList.add(cartModel);
                                    }
                                }

                                int cart_count = 0;

                                for (int i = 0; i<cartModelArrayList.size();i++){
                                    cart_count += Integer.parseInt(cartModelArrayList.get(i).getQty());
                                }
                                cartAdapter.notifyDataSetChanged();

                                tvProductCount.setText("Count : "+String.valueOf(cart_count));
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

    /**
     * This method is for placing order
     */
    public void placeOrderTask(){
        dialogView.showCustomSpinProgress(DistributorReviewActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("email", pref.getStringPreference(DistributorReviewActivity.this,Preferences.User_Email));
        postParam.put("ip", "12345678");
        postParam.put("mobile", pref.getStringPreference(DistributorReviewActivity.this,Preferences.User_Mobile));
        postParam.put("fname", pref.getStringPreference(DistributorReviewActivity.this,Preferences.User_fname));
        postParam.put("lname", pref.getStringPreference(DistributorReviewActivity.this,Preferences.User_lname));
        postParam.put("billing_country", "India");
        postParam.put("tax_amount", "0");
        postParam.put("order_lat", "0.0");
        postParam.put("order_lng", "0.0");
        postParam.put("comment", etComment.getText().toString());

        Log.d("order params>>",postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_DISTRIBUTOR_PLACE_ORDER, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                //Calling createActivityLogTask method
                                startActivity(new Intent(DistributorReviewActivity.this, DistributorOrderPlacedActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(DistributorReviewActivity.this, getResources().getString(R.string.app_name),
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