package com.b2bapp.onn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.LoginActivity;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout relOrderOnCall, relStoreVisit, relDistributorVisit, relActivityLog, relMyProfile,
            relLogout,relProductCatalogue, relScheme, relAddStore, relMyOrders, relDashboard, relSalesReport,
            relStoreWiseReport,relProductWiseReport, relEndVisit;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="TaskActivity";
    Preferences pref;

    String userId = "";
    String visitId = "";

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
    TextView tvName, tvDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        start_date = sdf2.format(new Date());

        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
        String currentDateandTime1 = sdf1.format(new Date());
        start_time = currentDateandTime1;

        userId = pref.getStringPreference(TaskActivity.this,Preferences.Id);
        visitId = pref.getStringPreference(TaskActivity.this,Preferences.LAST_VISIT_ID);

        tvName = (TextView) findViewById(R.id.tvName);
        tvDesignation = (TextView) findViewById(R.id.tvDesignation);

        tvName.setText("Welcome, " + pref.getStringPreference(TaskActivity.this, Preferences.User_fname) + " " +
                pref.getStringPreference(TaskActivity.this, Preferences.User_lname));

        if (pref.getStringPreference(TaskActivity.this, Preferences.User_User_type).equals("1")){

            tvDesignation.setText("Vice President");

        }else if(pref.getStringPreference(TaskActivity.this, Preferences.User_User_type).equals("2")){
            tvDesignation.setText("Regional Sales Manager");

        }else if(pref.getStringPreference(TaskActivity.this, Preferences.User_User_type).equals("3")){
            tvDesignation.setText("Area Sales Manager");

        }else if(pref.getStringPreference(TaskActivity.this, Preferences.User_User_type).equals("4")){
            tvDesignation.setText("Area Sales Executive");
        }else if(pref.getStringPreference(TaskActivity.this, Preferences.User_User_type).equals("5")){
            tvDesignation.setText("Distributor");
        }

        relOrderOnCall = (RelativeLayout) findViewById(R.id.relOrderOnCall);
        relStoreVisit = (RelativeLayout) findViewById(R.id.relStoreVisit);
        relDistributorVisit = (RelativeLayout) findViewById(R.id.relDistributorVisit);
        relActivityLog = (RelativeLayout) findViewById(R.id.relActivityLog);
        relMyProfile = (RelativeLayout) findViewById(R.id.relMyProfile);
        relLogout = (RelativeLayout) findViewById(R.id.relLogout);
        relProductCatalogue = (RelativeLayout) findViewById(R.id.relProductCatalogue);
        relScheme = (RelativeLayout) findViewById(R.id.relScheme);
        relMyOrders = (RelativeLayout) findViewById(R.id.relMyOrders);
        relAddStore = (RelativeLayout) findViewById(R.id.relAddStore);
        relDashboard = (RelativeLayout) findViewById(R.id.relDashboard);
        relSalesReport = (RelativeLayout) findViewById(R.id.relSalesReport);
        relStoreWiseReport = (RelativeLayout) findViewById(R.id.relStoreWiseReport);
        relProductWiseReport = (RelativeLayout) findViewById(R.id.relProductWiseReport);
        relEndVisit = (RelativeLayout) findViewById(R.id.relEndVisit);

        relOrderOnCall.setOnClickListener(this);
        relStoreVisit.setOnClickListener(this);
        relDistributorVisit.setOnClickListener(this);
        relActivityLog.setOnClickListener(this);
        relMyProfile.setOnClickListener(this);
        relLogout.setOnClickListener(this);
        relProductCatalogue.setOnClickListener(this);
        relScheme.setOnClickListener(this);
        relMyOrders.setOnClickListener(this);
        relAddStore.setOnClickListener(this);
        relDashboard.setOnClickListener(this);
        relSalesReport.setOnClickListener(this);
        relStoreWiseReport.setOnClickListener(this);
        relProductWiseReport.setOnClickListener(this);
        relEndVisit.setOnClickListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // method to get the location
        getLastLocation();
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.relOrderOnCall:
                GlobalVariable.order_type = "Order On Call";
                startActivity(new Intent(TaskActivity.this, StoreCallActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relStoreVisit:
                GlobalVariable.order_type = "Store Visit";
                startActivity(new Intent(TaskActivity.this, StoreListActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relDistributorVisit:
                startActivity(new Intent(TaskActivity.this, DistributorActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relActivityLog:
                startActivity(new Intent(TaskActivity.this, ActivityLogActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relMyProfile:
                startActivity(new Intent(TaskActivity.this, MyProfileActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relLogout:
                logoutAlertDialog(TaskActivity.this, "Logout Now!", "Do you want to exit from this application?");
                break;
            case R.id.relProductCatalogue:
                startActivity(new Intent(TaskActivity.this, ProductCatalogueActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relScheme:
                startActivity(new Intent(TaskActivity.this, SchemeActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relMyOrders:
                startActivity(new Intent(TaskActivity.this, MyOrdersActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relAddStore:
                startActivity(new Intent(TaskActivity.this, StoreAddActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relDashboard:
                startActivity(new Intent(TaskActivity.this, AseDashboardActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relSalesReport:
                startActivity(new Intent(TaskActivity.this, AseSalesReportActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relStoreWiseReport:
                GlobalVariable.store_report_date_from = "";
                GlobalVariable.store_report_date_to = "";
                GlobalVariable.store_report_collection = "";
                GlobalVariable.store_report_style_no = "";
                startActivity(new Intent(TaskActivity.this, AseStoreWiseReportActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relProductWiseReport:
                GlobalVariable.product_date_from = "";
                GlobalVariable.product_date_to = "";
                GlobalVariable.product_collection = "";
                GlobalVariable.product_collection = "";

                startActivity(new Intent(TaskActivity.this, AseProductWiseReportActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relEndVisit:
                endVisitAlertDialog(TaskActivity.this, "End Visit!", "Do you want to end this current visit?");
                break;
        }
    }

    /**
     * This method is for application logout
     * @param context
     * @param header
     * @param msg
     */
    public void logoutAlertDialog(final Activity context, String header, String msg){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.inflate_custom_alert_dialog, null);

        final android.app.AlertDialog alertD = new android.app.AlertDialog.Builder(context).create();
        TextView tvHeader=(TextView)promptView.findViewById(R.id.tvHeader);
        tvHeader.setText(header);
        TextView tvMsg=(TextView)promptView.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        Button btnCancel = (Button) promptView.findViewById(R.id.btnCancel);
        btnCancel.setText("Cancel");
        Button btnOk = (Button) promptView.findViewById(R.id.btnOk);
        btnOk.setText("Ok");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //pref.clearAllPref();
                pref.storeStringPreference(TaskActivity.this, Preferences.Id,"");
                pref.storeStringPreference(TaskActivity.this,Preferences.User_fname,"");
                pref.storeStringPreference(TaskActivity.this,Preferences.User_lname,"");
                pref.storeStringPreference(TaskActivity.this,Preferences.User_Email,"");
                pref.storeStringPreference(TaskActivity.this,Preferences.User_Mobile,"");
                pref.storeStringPreference(TaskActivity.this,Preferences.User_Employee_id,"");

                startActivity(new Intent(TaskActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                alertD.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertD.dismiss();

            }
        });

        alertD.setView(promptView);

        alertD.show();
    }

    /**
     * This method is for application logout
     * @param context
     * @param header
     * @param msg
     */
    public void endVisitAlertDialog(final Activity context, String header, String msg){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.inflate_custom_alert_dialog, null);

        final android.app.AlertDialog alertD = new android.app.AlertDialog.Builder(context).create();
        TextView tvHeader=(TextView)promptView.findViewById(R.id.tvHeader);
        tvHeader.setText(header);
        TextView tvMsg=(TextView)promptView.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        Button btnCancel = (Button) promptView.findViewById(R.id.btnCancel);
        btnCancel.setText("Cancel");
        Button btnOk = (Button) promptView.findViewById(R.id.btnOk);
        btnOk.setText("Ok");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endVisitTask();
                alertD.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertD.dismiss();

            }
        });

        alertD.setView(promptView);

        alertD.show();
    }

    /**
     * This method is for end visit
     */
    public void endVisitTask(){
        dialogView.showCustomSpinProgress(TaskActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("visit_id", visitId);
        postParam.put("end_date", start_date);
        postParam.put("end_time", start_time);
        postParam.put("end_location", location);
        postParam.put("end_lat", String.valueOf(lat));
        postParam.put("end_lat", String.valueOf(lon));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VISIT_END, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {

                                pref.storeStringPreference(TaskActivity.this,Preferences.LAST_VISIT_ID,"");
                                //Calling createActivityLogTask method
                                createActivityLogTask();
                            }else {
                                dialogView.showSingleButtonDialog(TaskActivity.this, getResources().getString(R.string.app_name),
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

        dialogView.showCustomSpinProgress(TaskActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("date", d);
        postParam.put("time", t);
        postParam.put("type", "Visit Ended");
        postParam.put("comment", "Your visit has been ended at "+GlobalVariable.selected_area);
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

                                startActivity(new Intent(TaskActivity.this, StartingActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(TaskActivity.this, getResources().getString(R.string.app_name),
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