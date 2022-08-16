package com.b2bapp.onn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.CartAdapter;
import com.b2bapp.onn.adapter.StorelistAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.db.DbHelper;
import com.b2bapp.onn.model.CartModel;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.model.UserModel;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.ConnectionStatus;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReviewOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private TextView tvStoreName, tvStoreCode, tvArea, tvBusinessName, tvProductName, tvProductCount;
    private EditText etComment;
    private Button btnPlaceOrder,btnContinueShopping;

    private ListView cartList;

    ArrayList<CartModel> cartModelArrayList = new ArrayList<CartModel>();
    CartAdapter cartAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="SplashActivity";
    Preferences pref;

    String userId = "";
    String userType = "";

    String total_quantity = "0";

    String location = "";
    double lat = 0;
    double lon = 0;

    //For location tracking
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    Geocoder geocoder;
    List<Address> addresses;

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        dbHelper = new DbHelper(ReviewOrderActivity.this);

        userType = pref.getStringPreference(ReviewOrderActivity.this,Preferences.User_User_type);

        if (userType.equals("3")){
            userId = GlobalVariable.ase_id_from_asm_dashboard;
        }else{
            userId = pref.getStringPreference(ReviewOrderActivity.this,Preferences.Id);
        }

        imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(this);

        tvStoreName = (TextView) findViewById(R.id.tvStoreName);
        tvStoreCode = (TextView) findViewById(R.id.tvStoreCode);
        tvArea = (TextView) findViewById(R.id.tvArea);
        tvBusinessName = (TextView) findViewById(R.id.tvBusinessName);
        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvProductCount = (TextView) findViewById(R.id.tvProductCount);

        tvStoreName.setText(GlobalVariable.storeModel.getStore_name());
        if (!GlobalVariable.storeModel.getStore_OCC_number().equals("null") && !GlobalVariable.storeModel.getStore_OCC_number().equals("")){
            tvStoreCode.setVisibility(View.VISIBLE);
            tvStoreCode.setText("#"+GlobalVariable.storeModel.getStore_OCC_number());
        }else{
            tvStoreCode.setVisibility(View.GONE);
        }

        tvArea.setText(GlobalVariable.storeModel.getArea());
        tvBusinessName.setText(GlobalVariable.storeModel.getBussiness_name());
        tvProductName.setText("Cart Items");

        etComment = (EditText) findViewById(R.id.etComment);

        cartList = (ListView) findViewById(R.id.cartList);

        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(this);
        btnContinueShopping = (Button) findViewById(R.id.btnContinueShopping);
        btnContinueShopping.setOnClickListener(this);

        cartAdapter = new CartAdapter(ReviewOrderActivity.this, cartModelArrayList);
        cartList.setAdapter(cartAdapter);

        //Calling getCartListTask method
        if (ConnectionStatus.checkConnectionStatus(ReviewOrderActivity.this)){
            //Calling getCartListTask method
            getCartListTask();
        }else{
            cartModelArrayList = dbHelper.allCartList();
            cartAdapter = new CartAdapter(ReviewOrderActivity.this, cartModelArrayList);
            cartList.setAdapter(cartAdapter);

            GlobalVariable.cartList = cartModelArrayList;

            int cart_count = 0;

            for (int i = 0; i<cartModelArrayList.size();i++){
                cart_count += Integer.parseInt(cartModelArrayList.get(i).getQty());
            }
            cartAdapter.notifyDataSetChanged();

            tvProductCount.setText("Count : "+String.valueOf(cart_count));
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // method to get the location
        getLastLocation();
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    public void reloadCart(){
        cartModelArrayList.clear();
        cartModelArrayList = dbHelper.allCartList();
        cartAdapter = new CartAdapter(ReviewOrderActivity.this, cartModelArrayList);
        cartList.setAdapter(cartAdapter);

        int cart_count = 0;

        for (int i = 0; i<cartModelArrayList.size();i++){
            cart_count += Integer.parseInt(cartModelArrayList.get(i).getQty());
        }
        cartAdapter.notifyDataSetChanged();

        tvProductCount.setText("Count : "+String.valueOf(cart_count));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.btnPlaceOrder:
                if (!etComment.getText().toString().equals("")){
                    OrderPreviewActivity.comment = etComment.getText().toString();
                    OrderPreviewActivity.lati = String.valueOf(lat);
                    OrderPreviewActivity.longi = String.valueOf(lon);
                    OrderPreviewActivity.location = location;
                    startActivity(new Intent(ReviewOrderActivity.this, OrderPreviewActivity.class));
                }else{
                    CToast.show(ReviewOrderActivity.this,"Please add a comment with your order");
                }


                //Checking cart model is blank or not
                /*if (cartModelArrayList.size()!=0){
//                    if (!etComment.getText().toString().equals("")){
//                        //Calling placeOrderTask method
//                        placeOrderTask();
//                    }else{
//                        CToast.show(ReviewOrderActivity.this,"Please add a comment with your order");
//                    }
                    placeOrderTask();

                }else{
                    CToast.show(ReviewOrderActivity.this,"Please add atleast one product to cart");
                }*/
                break;
            case R.id.btnContinueShopping:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReviewOrderActivity.this, ProductListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for getting cart list
     */
    public void getCartListTask(){
        dialogView.showCustomSpinProgress(ReviewOrderActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_CART_LIST+"/"+userId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        GlobalVariable.cartList.clear();
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                cartModelArrayList.clear();
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
                                    GlobalVariable.cartList = cartModelArrayList;
                                }

                                int cart_count = 0;

                                for (int i = 0; i<cartModelArrayList.size();i++){
                                    cart_count += Integer.parseInt(cartModelArrayList.get(i).getQty());
                                }
                                cartAdapter.notifyDataSetChanged();

                                tvProductCount.setText("Count : "+response.getString("total_quantity"));
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
        dialogView.showCustomSpinProgress(ReviewOrderActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("store_id", GlobalVariable.storeModel.getId());
        postParam.put("order_type", GlobalVariable.order_type);
        postParam.put("order_lat", String.valueOf(lat));
        postParam.put("order_lng", String.valueOf(lon));
        postParam.put("comment", etComment.getText().toString());

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
                                    startActivity(new Intent(ReviewOrderActivity.this, OrderPlacedActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }

                            }else {
                                dialogView.showSingleButtonDialog(ReviewOrderActivity.this, getResources().getString(R.string.app_name),
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

    /**
     * This method is for creating activity log
     */
    public void createActivityLogTask(){
        String d = "";
        String t = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        d = sdf.format(new Date());

        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        t = sdf1.format(new Date());

        dialogView.showCustomSpinProgress(ReviewOrderActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("date", d);
        postParam.put("time", t);
        postParam.put("type", "Order Upload");
        postParam.put("comment", "Took order from "+GlobalVariable.storeModel.getStore_name());
        postParam.put("lat", String.valueOf(lat));
        postParam.put("lng", String.valueOf(lon));
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

                                startActivity(new Intent(ReviewOrderActivity.this, OrderPlacedActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(ReviewOrderActivity.this, getResources().getString(R.string.app_name),
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

    //---------------------------------------------Section for location tracking---------------------------------------//
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location myLocation = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat = myLocation.getLatitude();
                            lon = myLocation.getLongitude();
                            Log.d("lat>>",String.valueOf(myLocation.getLatitude()));
                            Log.d("lon>>",String.valueOf(myLocation.getLongitude()));
                            try {
                                addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                Log.d("address>>",address);
                                Log.d("city>>",city);

                                if (address.equals("")){
                                    location = city;
                                }else{
                                    location = address;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            Log.d("lat>>",String.valueOf(mLastLocation.getLatitude()));
            Log.d("lon>>",String.valueOf(mLastLocation.getLongitude()));
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                Log.d("address>>",address);
                Log.d("city>>",city);

                if (address.equals("")){
                    location = city;
                }else{
                    location = address;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}