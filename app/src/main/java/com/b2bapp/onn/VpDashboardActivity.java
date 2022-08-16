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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.RsmAdapter;
import com.b2bapp.onn.adapter.StateAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.RsmModel;
import com.b2bapp.onn.model.StateModel;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.model.UserModel;
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

public class VpDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="VpDashboardActivity";
    Preferences pref;

    private ArrayList<StateModel> stateModelArrayList = new ArrayList<StateModel>();
    private ArrayList<RsmModel> rsmModelArrayList = new ArrayList<RsmModel>();

    StateAdapter stateAdapter;
    RsmAdapter rsmAdapter;

    private ListView stateList, rsmList;
    BarChart stateBarChart,rsmBarChart;
    private TextView tvName;

    String userId = "";

    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;

    BarData barData1;
    BarDataSet barDataSet1;
    ArrayList barEntriesArrayList1;

    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vp_dashboard);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(VpDashboardActivity.this,Preferences.Id);

        stateList = (ListView) findViewById(R.id.stateList);
        rsmList = (ListView) findViewById(R.id.rsmList);

        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(pref.getStringPreference(VpDashboardActivity.this,Preferences.User_fname) + " "+ pref.getStringPreference(VpDashboardActivity.this,Preferences.User_lname));

        stateAdapter = new StateAdapter(VpDashboardActivity.this, stateModelArrayList);
        stateList.setAdapter(stateAdapter);

        rsmAdapter = new RsmAdapter(VpDashboardActivity.this, rsmModelArrayList);
        rsmList.setAdapter(rsmAdapter);

        stateBarChart = findViewById(R.id.stateBarChart);
        //rsmBarChart = findViewById(R.id.rsmBarChart);

        //Calling getVpData method
        getVpData(userId);

        stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VpStateWiseRegioActivity.state = stateModelArrayList.get(i).getName();
                startActivity(new Intent(VpDashboardActivity.this, VpStateWiseRegioActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
    }

    /**
     * This method is for getting VP data
     * @param userId
     */
    private void getVpData(String userId){
        dialogView.showCustomSpinProgress(VpDashboardActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VP_REPORT, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray stateArray = response.getJSONArray("stateWiseReport");
                                JSONArray rsmArray = response.getJSONArray("rsmWiseReport");

                                if (stateArray.length()>0){
                                    for (int i = 0; i<stateArray.length();i++){
                                        JSONObject stateObj = stateArray.getJSONObject(i);
                                        StateModel stateModel = new StateModel();
                                        stateModel.setName(stateObj.getString("name"));
                                        stateModel.setValue(stateObj.getString("value"));
                                        stateModelArrayList.add(stateModel);
                                    }
                                }
                                stateAdapter.notifyDataSetChanged();

                                //getStateBarEntries();

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
                                dialogView.showSingleButtonDialog(VpDashboardActivity.this, getResources().getString(R.string.app_name),
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

        for (int i= 0; i<stateModelArrayList.size();i++){
            if (!stateModelArrayList.get(i).getValue().equals("null")){
                barEntriesArrayList.add(new BarEntry((i+1), Integer.parseInt(stateModelArrayList.get(i).getValue())));
            }else{
                barEntriesArrayList.add(new BarEntry((i+1), 0));
            }
        }

        barDataSet = new BarDataSet(barEntriesArrayList, "State Wise Report");
        barData = new BarData(barDataSet);
        stateBarChart.setData(barData);
        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);
        // setting text size
        barDataSet.setValueTextSize(16f);
        stateBarChart.getDescription().setEnabled(false);
    }

    private void getRsmBarEntries() {
        // creating a new array list
        barEntriesArrayList1 = new ArrayList<>();

        for (int i= 0; i<rsmModelArrayList.size();i++){
            if (!rsmModelArrayList.get(i).getValue().equals("null")){
                barEntriesArrayList1.add(new BarEntry((i+1), Integer.parseInt(rsmModelArrayList.get(i).getValue())));
            }else{
                barEntriesArrayList1.add(new BarEntry((i+1), 0));
            }
        }

        barDataSet1 = new BarDataSet(barEntriesArrayList1, "RSM Wise Report");
        barData1 = new BarData(barDataSet1);
        rsmBarChart.setData(barData1);
        // adding color to our bar data set.
        barDataSet1.setColors(ColorTemplate.MATERIAL_COLORS);
        // setting text color.
        barDataSet1.setValueTextColor(Color.BLACK);
        // setting text size
        barDataSet1.setValueTextSize(16f);
        rsmBarChart.getDescription().setEnabled(false);
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
        Intent intent = new Intent(VpDashboardActivity.this, VpStartingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}