package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.AsePrimaryReportAdapter;
import com.b2bapp.onn.adapter.AseSecondaryReportAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.AsePrimaryReportModel;
import com.b2bapp.onn.model.AseSecondaryReportModel;
import com.b2bapp.onn.model.VpStoreModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AseDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etStartDate, etEndDate;
    final Calendar myCalendar= Calendar.getInstance();

    private String ase = "";
//    private String start = "01-07-2022";
//    private String end = "20-07-2022";
    private String start = "";
    private String end = "";

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="AseDashboardActivity";
    Preferences pref;

    private ListView primaryList,secondaryList;
    private ArrayList<AsePrimaryReportModel> asePrimaryReportModelArrayList = new ArrayList<AsePrimaryReportModel>();
    AsePrimaryReportAdapter asePrimaryReportAdapter;
    private ArrayList<AseSecondaryReportModel> aseSecondaryReportModelArrayList = new ArrayList<AseSecondaryReportModel>();
    AseSecondaryReportAdapter aseSecondaryReportAdapter;

    private ImageView imgBack;

    private TextView tvSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ase_dashboard);

        String myFormat="dd-MM-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);

        //String d = String.valueOf(myCalendar.getTime());
        //Log.d("d>>",d);

        end = dateFormat.format(myCalendar.getTime());

        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        //String d1 = String.valueOf(c.getTime());
        //Log.d("d1>>",d1);

        start = dateFormat.format(c.getTime());

        getSupportActionBar().hide();

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        ase = pref.getStringPreference(AseDashboardActivity.this,Preferences.User_fname)
                + " "+ pref.getStringPreference(AseDashboardActivity.this,Preferences.User_lname);

        etStartDate=(EditText) findViewById(R.id.etStartDate);
        etEndDate=(EditText) findViewById(R.id.etEndDate);
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel1();
            }
        };
        DatePickerDialog.OnDateSetListener date1 =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel2();
            }
        };
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AseDashboardActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AseDashboardActivity.this,date1,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etStartDate.setText(start);
        etEndDate.setText(end);

        primaryList = (ListView) findViewById(R.id.primaryList);
        asePrimaryReportAdapter = new AsePrimaryReportAdapter(AseDashboardActivity.this, asePrimaryReportModelArrayList);
        primaryList.setAdapter(asePrimaryReportAdapter);

        secondaryList = (ListView) findViewById(R.id.secondaryList);
        aseSecondaryReportAdapter = new AseSecondaryReportAdapter(AseDashboardActivity.this, aseSecondaryReportModelArrayList);
        secondaryList.setAdapter(aseSecondaryReportAdapter);

        primaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AseDistributorOrderListActivity.coming_from = "dashboard";
                AseDistributorOrderListActivity.userId = asePrimaryReportModelArrayList.get(i).getDistributor_id();
                startActivity(new Intent(AseDashboardActivity.this, AseDistributorOrderListActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        secondaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalVariable.store_id = aseSecondaryReportModelArrayList.get(i).getRetailer_id();
                StoreOrderListActivity.coming_from = "dashboard";
                startActivity(new Intent(AseDashboardActivity.this, StoreOrderListActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        tvSearch = (TextView) findViewById(R.id.tvSearch);
        tvSearch.setOnClickListener(this);

        getAllData();
    }

    private void updateLabel1(){
        String myFormat="dd-MM-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etStartDate.setText(dateFormat.format(myCalendar.getTime()));
        start = dateFormat.format(myCalendar.getTime());
    }

    private void updateLabel2(){
        String myFormat="dd-MM-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etEndDate.setText(dateFormat.format(myCalendar.getTime()));
        end = dateFormat.format(myCalendar.getTime());
    }

    private void getAllData(){
        dialogView.showCustomSpinProgress(AseDashboardActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("ase", ase);
        postParam.put("from", start);
        postParam.put("to", end);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_ASE_DASHBOARD_REPORT, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                asePrimaryReportModelArrayList.clear();
                                aseSecondaryReportModelArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("Primary Sales|Distributor wise Daily Report");

                                if (dataArray.length()>0){
                                    for (int i = 0; i<dataArray.length();i++){
                                        JSONObject dataObj = dataArray.getJSONObject(i);
                                        AsePrimaryReportModel dataModel = new AsePrimaryReportModel();
                                        dataModel.setDistributor_id(dataObj.getString("distributor_id"));
                                        dataModel.setDistributor_name(dataObj.getString("distributor_name"));
                                        dataModel.setAmount(dataObj.getString("amount"));
                                        dataModel.setQty(dataObj.getString("qty"));
                                        if (!dataObj.getString("distributor_name").equals("null")){
                                            asePrimaryReportModelArrayList.add(dataModel);
                                        }

                                    }
                                }
                                asePrimaryReportAdapter.notifyDataSetChanged();

                                JSONArray dataArray1 = response.getJSONArray("Secondary Sales|Retailer wise Daily Report");

                                if (dataArray1.length()>0){
                                    for (int i = 0; i<dataArray1.length();i++){
                                        JSONObject dataObj = dataArray1.getJSONObject(i);
                                        AseSecondaryReportModel dataModel1 = new AseSecondaryReportModel();
                                        dataModel1.setRetailer_id(dataObj.getString("retailer_id"));
                                        dataModel1.setStore_name(dataObj.getString("store_name"));
                                        dataModel1.setAmount(dataObj.getString("amount"));
                                        dataModel1.setQty(dataObj.getString("qty"));
                                        aseSecondaryReportModelArrayList.add(dataModel1);
                                    }
                                }
                                aseSecondaryReportAdapter.notifyDataSetChanged();

                            }else {
                                dialogView.showSingleButtonDialog(AseDashboardActivity.this, getResources().getString(R.string.app_name),
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.tvSearch:
                getAllData();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AseDashboardActivity.this, TaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}