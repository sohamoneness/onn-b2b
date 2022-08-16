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
import android.widget.AdapterView;
import android.widget.Button;
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
import com.b2bapp.onn.adapter.AreaAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.LoginActivity;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.AreaModel;
import com.b2bapp.onn.model.CategoryModel;
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

public class StartingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStartDay;
    private TextView tvDate, tvTime,tvArea;
    private Spinner areaSpin;

    AreaAdapter areaAdapter;
    ArrayList<AreaModel> areaModelArrayList = new ArrayList<AreaModel>();

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="StartingActivity";
    Preferences pref;

    String userId = "";

    String location = "";
    double lat = 0;
    double lon = 0;

    //For location tracking
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    Geocoder geocoder;
    List<Address> addresses;

    String start_date = "";
    String start_time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(StartingActivity.this,Preferences.Id);
        Log.d("user>>",userId);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        areaSpin = (Spinner) findViewById(R.id.areaSpin);
        tvArea = (TextView) findViewById(R.id.tvArea);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());
        tvDate.setText(currentDateandTime);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        start_date = sdf2.format(new Date());

        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
        String currentDateandTime1 = sdf1.format(new Date());
        start_time = currentDateandTime1;
        tvTime.setText(currentDateandTime1);

        tvArea.setText(pref.getStringPreference(StartingActivity.this,Preferences.User_City));

        btnStartDay = (Button) findViewById(R.id.btnStartDay);
        btnStartDay.setOnClickListener(this);

        areaAdapter = new AreaAdapter(areaModelArrayList, StartingActivity.this);
        areaSpin.setAdapter(areaAdapter);

        areaSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalVariable.selected_area = areaModelArrayList.get(i).getName();
                pref.storeStringPreference(StartingActivity.this,Preferences.LAST_SELECTED_AREA,GlobalVariable.selected_area);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (ConnectionStatus.checkConnectionStatus(StartingActivity.this)){
            getAreaTask();

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            // method to get the location
            getLastLocation();
            geocoder = new Geocoder(this, Locale.getDefault());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnStartDay:
                if (ConnectionStatus.checkConnectionStatus(StartingActivity.this)){
                    startVisitTask();
                }else{
                    Log.d("Conenction status>>","not connected to internet");
                    startActivity(new Intent(StartingActivity.this, TaskActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
                break;
        }
    }

    public void getAreaTask(){
        dialogView.showCustomSpinProgress(StartingActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_AREA_LIST+"/userid/"+userId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->areas>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray categoriesArray = response.getJSONArray("data");

                                if (categoriesArray.length() > 0) {
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        AreaModel categoryModel = new AreaModel();

                                        categoryModel.setId(categoryObj.getString("id"));
                                        categoryModel.setName(categoryObj.getString("name"));
                                        categoryModel.setState(categoryObj.getString("state"));

                                        areaModelArrayList.add(categoryModel);
                                    }
                                }

                                areaAdapter.notifyDataSetChanged();

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
     * This method is for start visit
     */
    public void startVisitTask(){
        dialogView.showCustomSpinProgress(StartingActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("area", GlobalVariable.selected_area);
        postParam.put("start_date", start_date);
        postParam.put("start_time", start_time);
        postParam.put("start_location", location);
        postParam.put("start_lat", String.valueOf(lat));
        postParam.put("start_lon", String.valueOf(lon));

        Log.d("order params>>",postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VISIT_START, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                String visit_id = response.getString("visit_id");
                                pref.storeStringPreference(StartingActivity.this,Preferences.LAST_VISIT_ID,visit_id);
                                //Calling createActivityLogTask method
                                createActivityLogTask();
                            }else {
                                dialogView.showSingleButtonDialog(StartingActivity.this, getResources().getString(R.string.app_name),
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

        dialogView.showCustomSpinProgress(StartingActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("date", d);
        postParam.put("time", t);
        postParam.put("type", "Visit Started");
        postParam.put("comment", "Your visit has been started at "+GlobalVariable.selected_area);
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

                                startActivity(new Intent(StartingActivity.this, TaskActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(StartingActivity.this, getResources().getString(R.string.app_name),
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

    @Override
    public void onBackPressed() {
        Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
        homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
        homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreenIntent);
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