package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.RegionAdapter;
import com.b2bapp.onn.adapter.Rsm1Adapter;
import com.b2bapp.onn.adapter.RsmAdapter;
import com.b2bapp.onn.adapter.StateAdapter;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.RegionModel;
import com.b2bapp.onn.model.RsmModel;
import com.b2bapp.onn.model.StateModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VpStateWiseRegioActivity extends AppCompatActivity implements View.OnClickListener {

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="VpStateWiseRegioActivity";
    Preferences pref;

    String userId = "";
    public static String state = "";

    private ArrayList<RegionModel> regionModelArrayList = new ArrayList<RegionModel>();
    private ArrayList<RsmModel> rsmModelArrayList = new ArrayList<RsmModel>();

    RegionAdapter regionAdapter;
    Rsm1Adapter rsmAdapter;

    private ListView regionList, rsmList;
    private ImageView imgBack;

    BarChart regionBarChart;

    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vp_state_wise_regio);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(VpStateWiseRegioActivity.this,Preferences.Id);

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(VpStateWiseRegioActivity.this,Preferences.Id);

        regionList = (ListView) findViewById(R.id.regionList);
        rsmList = (ListView) findViewById(R.id.rsmList);

        regionAdapter = new RegionAdapter(VpStateWiseRegioActivity.this, regionModelArrayList);
        regionList.setAdapter(regionAdapter);

        rsmAdapter = new Rsm1Adapter(VpStateWiseRegioActivity.this, rsmModelArrayList);
        rsmList.setAdapter(rsmAdapter);

        getVpDataAll(userId,state);

        regionBarChart = findViewById(R.id.regionBarChart);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        rsmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VpAsmActivity.state = state;
                VpAsmActivity.rsm = rsmModelArrayList.get(i).getName();
                startActivity(new Intent(VpStateWiseRegioActivity.this, VpAsmActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    private void getVpDataAll(String userId, String state){
        dialogView.showCustomSpinProgress(VpStateWiseRegioActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("state", state);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VP_REPORT_ALL, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray stateArray = response.getJSONArray("region");
                                JSONArray rsmArray = response.getJSONArray("rsm");

                                if (stateArray.length()>0){
                                    for (int i = 0; i<stateArray.length();i++){
                                        JSONObject stateObj = stateArray.getJSONObject(i);
                                        RegionModel regionModel = new RegionModel();
                                        regionModel.setName(stateObj.getString("area"));
                                        regionModel.setValue(stateObj.getString("value"));
                                        regionModelArrayList.add(regionModel);
                                    }
                                }
                                regionAdapter.notifyDataSetChanged();

                                getStateBarEntries();

                                if (rsmArray.length()>0){
                                    for (int i = 0; i<rsmArray.length();i++){
                                        JSONObject rsmObj = rsmArray.getJSONObject(i);
                                        RsmModel rsmModel = new RsmModel();
                                        rsmModel.setName(rsmObj.getString("name"));
                                        rsmModel.setValue(rsmObj.getString("value"));
                                        rsmModelArrayList.add(rsmModel);
                                    }
                                }
                                rsmAdapter.notifyDataSetChanged();

                                //getRsmBarEntries();

                            }else {
                                dialogView.showSingleButtonDialog(VpStateWiseRegioActivity.this, getResources().getString(R.string.app_name),
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

    private void getStateBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        for (int i= 0; i<regionModelArrayList.size();i++){
            if (!regionModelArrayList.get(i).getValue().equals("null")){
                barEntriesArrayList.add(new BarEntry((i+1), Integer.parseInt(regionModelArrayList.get(i).getValue())));
            }else{
                barEntriesArrayList.add(new BarEntry((i+1), 0));
            }
        }

        barDataSet = new BarDataSet(barEntriesArrayList, "State Wise Report");
        barData = new BarData(barDataSet);
        regionBarChart.setData(barData);
        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);
        // setting text size
        barDataSet.setValueTextSize(16f);
        regionBarChart.getDescription().setEnabled(false);
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
        Intent intent = new Intent(VpStateWiseRegioActivity.this, VpDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}