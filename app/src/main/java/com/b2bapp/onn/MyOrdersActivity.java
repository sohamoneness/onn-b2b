package com.b2bapp.onn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.b2bapp.onn.adapter.StoreOrderAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.db.DbHelper;
import com.b2bapp.onn.model.OrderDbModel;
import com.b2bapp.onn.model.OrderModel;
import com.b2bapp.onn.model.OrderProductModel;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyOrdersActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView orderList;
    private ImageView imgBack,imgSync;
    Spinner storeSp;
    EditText etStartDate, etEndDate;
    TextView tvSearch;
    String range;
    final Calendar myCalendar= Calendar.getInstance();
    final Calendar myCalendar1= Calendar.getInstance();

    ArrayList<OrderModel> orderModelArrayList = new ArrayList<OrderModel>();
    List<StoreModel> allStoreList = new ArrayList<>();
    List<String> allStoreName = new ArrayList<>();
    StoreOrderAdapter storeOrderAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="StoreOrderListActivity";
    Preferences pref;

    String from = "", to = "";

    private String userId = "";
    private String aseName = "";
    public static String isFrom = "";
    String storeID="";

    //For location tracking
    String location = "";
    double lat = 0;
    double lon = 0;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    Geocoder geocoder;
    List<Address> addresses;

    DbHelper dbHelper;

    int local_order_count = 0;

    String last_order_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        dbHelper = new DbHelper(MyOrdersActivity.this);

        storeSp = (Spinner) findViewById(R.id.storeSp);
        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etStartDate.setOnClickListener(this);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
        etEndDate.setOnClickListener(this);
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        tvSearch.setOnClickListener(this);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        imgSync = (ImageView) findViewById(R.id.imgSync);
        imgSync.setOnClickListener(this);

        if (isFrom.equals("asm")){
            userId = GlobalVariable.ase_id_from_asm_dashboard;
            aseName = GlobalVariable.ase_name;
            Log.d("ASE_NAME>>", aseName + ", " + userId);
        }else{
            aseName = pref.getStringPreference(MyOrdersActivity.this,Preferences.User_fname) + " " + pref.getStringPreference(MyOrdersActivity.this, Preferences.User_lname);
            Log.d("ASE_NAME>>", aseName);
            userId = pref.getStringPreference(MyOrdersActivity.this,Preferences.Id);
        }

        orderList = (ListView) findViewById(R.id.orderList);
        storeOrderAdapter = new StoreOrderAdapter(MyOrdersActivity.this,orderModelArrayList);
        orderList.setAdapter(storeOrderAdapter);


        getAllStores();

        ArrayList<OrderDbModel> orderDbModelArrayList = new ArrayList<OrderDbModel>();
        orderDbModelArrayList = dbHelper.allOrderList();

        for (int i=0;i<orderDbModelArrayList.size();i++){
            OrderModel orderModel = new OrderModel();
            orderModel.setOrder_no(orderDbModelArrayList.get(i).getId());
            orderModel.setOrder_date(orderDbModelArrayList.get(i).getOrder_date());

            ArrayList<OrderProductModel> orderProductModelArrayList = new ArrayList<OrderProductModel>();
            orderProductModelArrayList = dbHelper.orderWiseProductList(orderModel.getOrder_no());

            int q = 0;
            for (int j=0;j<orderProductModelArrayList.size();j++){
                q+= Integer.parseInt(orderProductModelArrayList.get(j).getQty());
            }

            orderModel.setProduct_count(String.valueOf(q));
            orderModelArrayList.add(orderModel);
        }

        local_order_count = orderDbModelArrayList.size();
        Log.d("local_order>>",String.valueOf(local_order_count));
        storeOrderAdapter = new StoreOrderAdapter(MyOrdersActivity.this,orderModelArrayList);
        orderList.setAdapter(storeOrderAdapter);

        if (ConnectionStatus.checkConnectionStatus(MyOrdersActivity.this)){
            //Calling getStoreListTask method
            getStoreListTask();
        }
        //getStoreListTask();
        //Calling getStoreListTask method

        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StoreOrderDetailsActivity.orderModel = orderModelArrayList.get(i);
                StoreOrderDetailsActivity.coming_from = "user";
                startActivity(new Intent(MyOrdersActivity.this, StoreOrderDetailsActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // method to get the location
        getLastLocation();
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void getAllStores() {
        //dialogView.showCustomSpinProgress(MyOrdersActivity.this);

        Map<String, String> postParam = new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                //WebServices.URL_STORE_VISIT_END+"/"+pref.getStringPreference(TaskActivity.this,Preferences.Visit_id), new JSONObject(postParam),
                WebServices.URL_ALL_STORE_LIST + "?ase=" + aseName, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        //dialogView.dismissCustomSpinProgress();

                        try {
                            if (!response.getBoolean("error")) {


                                JSONArray jsonArray = response.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++){

                                    StoreModel sm = new StoreModel();
                                    sm.setId(jsonArray.getJSONObject(i).getString("id"));
                                    sm.setStore_name(jsonArray.getJSONObject(i).getString("store_name"));
                                    allStoreList.add(sm);
                                    allStoreName.add(jsonArray.getJSONObject(i).getString("store_name"));
                                }
                                if (allStoreName.size()>0){
                                    ArrayAdapter<String> spinnerPhoneArrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, allStoreName);
                                    spinnerPhoneArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
                                    storeSp.setAdapter(spinnerPhoneArrayAdapter2);
                                    storeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                            String storeName = allStoreName.get(i);
                                            for (int j = 0; j < allStoreList.size(); j++){

                                                if (allStoreList.get(j).getStore_name().equals(storeName)){
                                                    storeID = allStoreList.get(j).getId();
                                                    if (j > 0){
                                                        getStoreListTask();
                                                    }

                                                    //Toast.makeText(MyOrdersActivity.this, ""+storeName + ", " + storeID, Toast.LENGTH_SHORT).show();
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {



                                        }
                                    });
                                }

                            } else {
                                //dialogView.dismissCustomSpinProgress();
                                dialogView.showSingleButtonDialog(MyOrdersActivity.this, getResources().getString(R.string.app_name),
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
                if (error.equals("invalid_credentials")) {
                    Log.e("invalid>>", "You have entered a wrong credentials");
                }
               // dialogView.dismissCustomSpinProgress();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.etStartDate:
                range = "from";
                DatePickerDialog datePickerDialog = new DatePickerDialog(MyOrdersActivity.this, date1, myCalendar
                        .get(android.icu.util.Calendar.YEAR), myCalendar.get(android.icu.util.Calendar.MONTH),
                        myCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
                break;
            case R.id.etEndDate:
                range = "to";
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(MyOrdersActivity.this, date2, myCalendar1
                        .get(android.icu.util.Calendar.YEAR), myCalendar1.get(android.icu.util.Calendar.MONTH),
                        myCalendar1.get(android.icu.util.Calendar.DAY_OF_MONTH));
                datePickerDialog1.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog1.show();
                break;
            case R.id.tvSearch:
                if (from.equals("") && to.equals("")){
                    dialogView.showSingleButtonDialog(MyOrdersActivity.this, getResources().getString(R.string.app_name), "Please select Start and End date to filter.");
                }else if (from.equals("") || to.equals("")){
                    dialogView.showSingleButtonDialog(MyOrdersActivity.this, getResources().getString(R.string.app_name), "Please select Start and End date to filter.");
                }else{
                    getStoreListTask();
                }
                break;
            case R.id.imgSync:
                if (ConnectionStatus.checkConnectionStatus(MyOrdersActivity.this)){
                    syncOrderDataFromLocal();
                }else{
                    CToast.show(MyOrdersActivity.this,"You need internet connection to sync order data");
                }

                break;
        }
    }

    final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(android.icu.util.Calendar.YEAR, year);
            myCalendar.set(android.icu.util.Calendar.MONTH, monthOfYear);
            myCalendar.set(android.icu.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            /*if(datetype.equalsIgnoreCase("From"))
            {
                updateLabel(etFrom);
            }
            else if(datetype.equalsIgnoreCase("To"))
            {*/
            //if (range.equals("from")){
            updateLabel(etStartDate);
            //}else if (range.equals("to")){
            //    updateLabel(tvTo);
            //}

            // }


        }

    };
    final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar1.set(android.icu.util.Calendar.YEAR, year);
            myCalendar1.set(android.icu.util.Calendar.MONTH, monthOfYear);
            myCalendar1.set(android.icu.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            /*if(datetype.equalsIgnoreCase("From"))
            {
                updateLabel(etFrom);
            }
            else if(datetype.equalsIgnoreCase("To"))
            {*/
            /*if (range.equals("from")){
                updateLabel(tvFrom);
            }else if (range.equals("to")){*/
            updateLabel(etEndDate);
            //}

            // }


        }

    };

    private void updateLabel(EditText editText) {
        //   String myFormat = "yyyy-MM-dd"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());


        if (range.equals("from")) {
            from = sdf.format(myCalendar.getTime());
            editText.setText(sdf.format(myCalendar.getTime()));
        } else if (range.equals("to")) {
            to = sdf.format(myCalendar.getTime());
            editText.setText(sdf.format(myCalendar1.getTime()));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFrom.equals("asm")){
            Intent intent = new Intent(MyOrdersActivity.this, AsmDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(MyOrdersActivity.this, TaskActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    /**
     * This method is for getting order list
     */
    public void getStoreListTask(){
        dialogView.showCustomSpinProgress(MyOrdersActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id",userId);
        postParam.put("store_id", storeID);
        postParam.put("date_from", from);
        postParam.put("date_to", to);

        Log.d("GetOrderParam>>", postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
        //JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
               // WebServices.URL_MY_ORDERS+"/"+userId, new JSONObject(postParam),
                WebServices.URL_MY_ORDERS_FILTER, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->orders>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                //orderModelArrayList.clear();
                                JSONArray ordersArray = response.getJSONArray("data");

                                if (ordersArray.length() > 0) {
                                    for (int i = 0; i < ordersArray.length(); i++) {
                                        JSONObject orderObj = ordersArray.getJSONObject(i);
                                        OrderModel orderModel = new OrderModel();

                                        orderModel.setId(orderObj.getString("id"));
                                        //orderModel.setStore_name(orderObj.getJSONObject("stores").getString("store_name"));
                                        //orderModel.setStore_code(orderObj.getJSONObject("stores").getString("store_OCC_number"));
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

    public void syncOrderDataFromLocal(){
        int local_count = 0;
        ArrayList<OrderDbModel> orderDbModelArrayList = new ArrayList<OrderDbModel>();
        orderDbModelArrayList = dbHelper.allOrderList();
        local_count = orderDbModelArrayList.size();

        for (int i=0;i<orderDbModelArrayList.size();i++){
            localOrderTask(orderDbModelArrayList.get(i).getId(),orderDbModelArrayList.get(i).getStore_id(),
                    orderDbModelArrayList.get(i).getOrder_date(), orderDbModelArrayList.get(i).getComment());

            local_count = local_count-1;

            if (local_count==0){
                CToast.show(MyOrdersActivity.this,"All order has been synced with server");
                break;
            }
        }

        //dbHelper.deleteOrderData();
        //dbHelper.deleteOrderProductData();
        //getStoreListTask();
        if (local_count==0){
            Log.d("out>>","I am out");
            dbHelper.deleteOrderData();
            startActivity(new Intent(MyOrdersActivity.this, TaskActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }

    }

    /**
     * This method is for insert local order data to server
     */
    public void localOrderTask(String order_no, String store_id,String created_at, String comment){
        //dialogView.showCustomSpinProgress(MyOrdersActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id",userId);
        postParam.put("store_id", store_id);
        postParam.put("created_at", created_at);
        postParam.put("comment", comment);
        postParam.put("order_lat", String.valueOf(lat));
        postParam.put("order_lng", String.valueOf(lon));

        Log.d("GetOrderParam>>", postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_CREATE_ORDER_FROM_LOCAL, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->local orders>>", response.toString());
                        //dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                last_order_id = response.getString("order_id");
                                Log.d("last_order>>",last_order_id);
                                ArrayList<OrderProductModel> orderProductModelArrayList = new ArrayList<OrderProductModel>();
                                orderProductModelArrayList = dbHelper.orderWiseProductList(order_no);

                                for (int i=0;i<orderProductModelArrayList.size();i++){
                                    localOrderProductTask(last_order_id,orderProductModelArrayList.get(i).getProduct_name(),
                                            orderProductModelArrayList.get(i).getProduct_style(),orderProductModelArrayList.get(i).getColor(),
                                            orderProductModelArrayList.get(i).getSize(),orderProductModelArrayList.get(i).getQty());
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //dialogView.dismissCustomSpinProgress();
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

    public void localOrderProductTask(String order_id,String product_name, String product_style_no,String color,
                                      String size, String qty){
        //dialogView.showCustomSpinProgress(MyOrdersActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("order_id",order_id);
        postParam.put("product_name", product_name);
        postParam.put("product_style_no", product_style_no);
        postParam.put("color", color);
        postParam.put("size", size);
        postParam.put("qty", qty);

        Log.d("GetOrderParam>>", postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_SAVE_ORDER_PRODUCT_FROM_LOCAL, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->local orders>>", response.toString());
                        //dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                Log.d("Product success","Product inserted successfull against order :"+order_id);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //dialogView.dismissCustomSpinProgress();
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