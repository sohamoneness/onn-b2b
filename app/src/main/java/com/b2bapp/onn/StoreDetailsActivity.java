package com.b2bapp.onn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.db.DbHelper;
import com.b2bapp.onn.model.CartModel;
import com.b2bapp.onn.model.StoreModel;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StoreDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack,imgStore;
    private Button btnPlaceOrder, btnNoOrder,btnSubmitNoOrder , btnNoOrderCancel;
    private TextView tvStoreName, tvStoreCode, tvBusinessName,tvStoreAddress;
    private LinearLayout liBackGround;
    private RelativeLayout relNoOrderReason;
    private RelativeLayout relNoReason1, relNoReason2, relNoReason3, relNoReason4, relNoReason5, relNoReason6;
    private ImageView imgReason1,imgReason2, imgReason3, imgReason4, imgReason5, imgReason6;
    private LinearLayout liProductWiseSales, liTotalOrder, liLastVisit,liPlaceOrder,liNoOrder;
    private EditText etReason;

    private RelativeLayout relPlaceOrder, relNoOrder, relTotalOrderHistory;

    public static StoreModel storeModel;

    String reason = "";

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="SplashActivity";
    Preferences pref;

    String userId = "";

    ImageLoader imageLoader;
    DisplayImageOptions options;

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
        setContentView(R.layout.activity_store_details);

        getSupportActionBar().hide();

        dbHelper = new DbHelper(StoreDetailsActivity.this);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(StoreDetailsActivity.this));
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.default_image)
                //.showImageForEmptyUri(R.drawable.default_image)
                // .showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        dialogView.showSingleButtonDialog(StoreDetailsActivity.this, getResources().getString(R.string.app_name),
                getResources().getString(R.string.your_location_is_tracked));

        userId = pref.getStringPreference(StoreDetailsActivity.this,Preferences.Id);

        //GlobalVariable.storeModel = storeModel;

        imgBack = (ImageView) findViewById(R.id.imgBack);

        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);
        btnNoOrder = (Button) findViewById(R.id.btnNoOrder);
        btnSubmitNoOrder = (Button) findViewById(R.id.btnSubmitNoOrder);
        btnNoOrderCancel = (Button) findViewById(R.id.btnNoOrderCancel);

        liBackGround = (LinearLayout) findViewById(R.id.liBackGround);

        relNoOrderReason = (RelativeLayout) findViewById(R.id.relNoOrderReason);

        imgBack.setOnClickListener(this);
        btnPlaceOrder.setOnClickListener(this);
        btnNoOrder.setOnClickListener(this);
        btnSubmitNoOrder.setOnClickListener(this);
        btnNoOrderCancel.setOnClickListener(this);

        tvStoreName = (TextView) findViewById(R.id.tvStoreName);
        tvStoreCode = (TextView) findViewById(R.id.tvStoreCode);
        tvBusinessName = (TextView) findViewById(R.id.tvBusinessName);
        tvStoreAddress = (TextView) findViewById(R.id.tvStoreAddress);
        imgStore = (ImageView) findViewById(R.id.imgStore);

        relNoReason1 = (RelativeLayout) findViewById(R.id.relNoReason1);
        relNoReason2 = (RelativeLayout) findViewById(R.id.relNoReason2);
        relNoReason3 = (RelativeLayout) findViewById(R.id.relNoReason3);
        relNoReason4 = (RelativeLayout) findViewById(R.id.relNoReason4);
        relNoReason5 = (RelativeLayout) findViewById(R.id.relNoReason5);
        relNoReason6 = (RelativeLayout) findViewById(R.id.relNoReason6);

        relNoReason1.setOnClickListener(this);
        relNoReason2.setOnClickListener(this);
        relNoReason3.setOnClickListener(this);
        relNoReason4.setOnClickListener(this);
        relNoReason5.setOnClickListener(this);
        relNoReason6.setOnClickListener(this);

        imgReason1 = (ImageView) findViewById(R.id.imgReason1);
        imgReason2 = (ImageView) findViewById(R.id.imgReason2);
        imgReason3 = (ImageView) findViewById(R.id.imgReason3);
        imgReason4 = (ImageView) findViewById(R.id.imgReason4);
        imgReason5 = (ImageView) findViewById(R.id.imgReason5);
        imgReason6 = (ImageView) findViewById(R.id.imgReason6);

        tvStoreName.setText(storeModel.getStore_name());
        if (!storeModel.getStore_OCC_number().equals("null") && !storeModel.getStore_OCC_number().equals("")) {
            tvStoreCode.setText(storeModel.getStore_OCC_number());
            tvStoreCode.setVisibility(View.VISIBLE);
        }else{
            tvStoreCode.setVisibility(View.GONE);
        }
        tvBusinessName.setText(storeModel.getBussiness_name());
        tvStoreAddress.setText(storeModel.getAddress()+", "+storeModel.getArea()+", "+storeModel.getState()+" "+storeModel.getPin());

        //liProductWiseSales = (LinearLayout) findViewById(R.id.liProductWiseSales);
        liTotalOrder = (LinearLayout) findViewById(R.id.liTotalOrder);
        //liLastVisit = (LinearLayout) findViewById(R.id.liLastVisit);
        liPlaceOrder = (LinearLayout) findViewById(R.id.liPlaceOrder);
        liNoOrder = (LinearLayout) findViewById(R.id.liNoOrder);
        //liProductWiseSales.setOnClickListener(this);
        liTotalOrder.setOnClickListener(this);
        //liLastVisit.setOnClickListener(this);
        liPlaceOrder.setOnClickListener(this);
        liNoOrder.setOnClickListener(this);

        imageLoader.displayImage(WebServices.IMAGE_BASE_URL+storeModel.getImage(), imgStore, options);

        etReason = (EditText) findViewById(R.id.etReason);

        relPlaceOrder = (RelativeLayout) findViewById(R.id.relPlaceOrder);
        relNoOrder = (RelativeLayout) findViewById(R.id.relNoOrder);
        relTotalOrderHistory = (RelativeLayout) findViewById(R.id.relTotalOrderHistory);
        relPlaceOrder.setOnClickListener(this);
        relNoOrder.setOnClickListener(this);
        relTotalOrderHistory.setOnClickListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // method to get the location
        getLastLocation();
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StoreDetailsActivity.this, StoreListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.btnPlaceOrder:
//                startActivity(new Intent(StoreDetailsActivity.this, ProductListActivity.class));
//                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//                finish();
                if (ConnectionStatus.checkConnectionStatus(StoreDetailsActivity.this)){
                    clearCartListTask();
                }else{
                    dbHelper.deleteCartData();
                    startActivity(new Intent(StoreDetailsActivity.this, ProductListActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
                break;
            case R.id.btnNoOrder:
                liBackGround.setVisibility(View.VISIBLE);
                relNoOrderReason.setVisibility(View.VISIBLE);
                break;
            case R.id.relPlaceOrder:
//                startActivity(new Intent(StoreDetailsActivity.this, ProductListActivity.class));
//                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//                finish();
                if (ConnectionStatus.checkConnectionStatus(StoreDetailsActivity.this)){
                    clearCartListTask();
                }else{
                    dbHelper.deleteCartData();
                    startActivity(new Intent(StoreDetailsActivity.this, ProductListActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
                break;
            case R.id.relNoOrder:
                liBackGround.setVisibility(View.VISIBLE);
                relNoOrderReason.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSubmitNoOrder:
                if (!reason.equals("")){
                    //Calling noOrderReasonUpdateTask method
                    noOrderReasonUpdateTask();
                }else{
                    CToast.show(StoreDetailsActivity.this,"Please select atleast one reason");
                }

                break;
            case R.id.btnNoOrderCancel:
                liBackGround.setVisibility(View.GONE);
                relNoOrderReason.setVisibility(View.GONE);
                break;
            case R.id.relNoReason1:
                reason = "Product Related Issue";
                imgReason1.setImageDrawable(getResources().getDrawable(R.drawable.radio_fill));
                imgReason2.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason3.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason4.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason5.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason6.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                etReason.setVisibility(View.GONE);
                break;
            case R.id.relNoReason2:
                reason = "Distributor Related Issue";
                imgReason2.setImageDrawable(getResources().getDrawable(R.drawable.radio_fill));
                imgReason1.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason3.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason4.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason5.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason6.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                etReason.setVisibility(View.GONE);
                break;
            case R.id.relNoReason3:
                reason = "Competitor Related Issue";
                imgReason3.setImageDrawable(getResources().getDrawable(R.drawable.radio_fill));
                imgReason2.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason1.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason4.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason5.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason6.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                etReason.setVisibility(View.GONE);
                break;
            case R.id.relNoReason4:
                reason = "Comapny Related Issue";
                imgReason4.setImageDrawable(getResources().getDrawable(R.drawable.radio_fill));
                imgReason2.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason3.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason1.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason5.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason6.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                etReason.setVisibility(View.GONE);
                break;
            case R.id.relNoReason5:
                reason = "Shop Related Issue";
                imgReason5.setImageDrawable(getResources().getDrawable(R.drawable.radio_fill));
                imgReason2.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason3.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason4.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason1.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason6.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                etReason.setVisibility(View.GONE);
                break;
            case R.id.relNoReason6:
                reason = "more";
                imgReason6.setImageDrawable(getResources().getDrawable(R.drawable.radio_fill));
                imgReason2.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason3.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason4.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason5.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                imgReason1.setImageDrawable(getResources().getDrawable(R.drawable.radio_empty));
                etReason.setVisibility(View.VISIBLE);
                break;
//            case R.id.liProductWiseSales:
//
//                break;
            case R.id.relTotalOrderHistory:
                StoreOrderListActivity.coming_from = "store_details";
                StoreOrderListActivity.storeModel = storeModel;
                startActivity(new Intent(StoreDetailsActivity.this, StoreOrderListActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
//            case R.id.liLastVisit:
//                StoreVisitActivity.storeId = storeModel.getId();
//                startActivity(new Intent(StoreDetailsActivity.this, StoreVisitActivity.class));
//                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//                finish();
//                break;
        }
    }

    /**
     * This method is for updating reason for not placing order
     */
    public void noOrderReasonUpdateTask(){
        String d = "";
        String t = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        d = sdf.format(new Date());

        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        t = sdf1.format(new Date());

        dialogView.showCustomSpinProgress(StoreDetailsActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("store_id", GlobalVariable.storeModel.getId());
        postParam.put("location", location);
        //postParam.put("comment", reason);
        postParam.put("lat", String.valueOf(lat));
        postParam.put("lng", String.valueOf(lon));
        postParam.put("date", d);
        postParam.put("time", t);
        if (!reason.equals("more")){
            postParam.put("comment", "No order placed at "+GlobalVariable.storeModel.getStore_name()+" due to "+reason);
        }else{
            postParam.put("comment", "No order placed at "+GlobalVariable.storeModel.getStore_name()+" due to "+etReason.getText().toString());
        }

        Log.d("params no order>>",postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_NO_ORDER_REASON_UPDATE, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                //Calling createActivityLogTask method
                                createActivityLogTask();
                            }else {
                                dialogView.showSingleButtonDialog(StoreDetailsActivity.this, getResources().getString(R.string.app_name),
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

        dialogView.showCustomSpinProgress(StoreDetailsActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("date", d);
        postParam.put("time", t);
        postParam.put("type", "No Order Placed");
        if (!reason.equals("more")){
            postParam.put("comment", "No order placed at "+GlobalVariable.storeModel.getStore_name()+" due to "+reason);
        }else{
            postParam.put("comment", "No order placed at "+GlobalVariable.storeModel.getStore_name()+" due to "+etReason.getText().toString());
        }

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
                                dialogView.showSingleButtonDialog(StoreDetailsActivity.this, getResources().getString(R.string.app_name),
                                        "Reason for not placing order has been updated successfully");

                                liBackGround.setVisibility(View.GONE);
                                relNoOrderReason.setVisibility(View.GONE);
                            }else {
                                dialogView.showSingleButtonDialog(StoreDetailsActivity.this, getResources().getString(R.string.app_name),
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
     * This method is for getting cart list
     */
    public void clearCartListTask(){
        dialogView.showCustomSpinProgress(StoreDetailsActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_CART_CLEAR+"/"+userId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                startActivity(new Intent(StoreDetailsActivity.this, ProductListActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
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