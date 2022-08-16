package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.AsmAdapter;
import com.b2bapp.onn.adapter.RegionAdapter;
import com.b2bapp.onn.adapter.Rsm1Adapter;
import com.b2bapp.onn.adapter.VpDistributorAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.AsmModel;
import com.b2bapp.onn.model.RegionModel;
import com.b2bapp.onn.model.RsmModel;
import com.b2bapp.onn.model.VpDistributorModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VpReportActivity extends AppCompatActivity implements View.OnClickListener {

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="VpDistributorActivity";
    Preferences pref;

    String userId = "";
    String userState = "";

    ArrayList<VpDistributorModel> distributorModelArrayList = new ArrayList<VpDistributorModel>();
    ArrayList<VpDistributorModel> distributorModelArrayList1 = new ArrayList<VpDistributorModel>();
    ListView distributorList;
    VpDistributorAdapter vpDistributorAdapter;

    private EditText etKeyword;
    private Spinner spinState;
    private TextView tvSearch;
    String[] states = { "Select State","Delhi", "Haryana", "Himachal Pradesh","Jammu and Kashmir","Maharashtra",
    "Punjab","Rajasthan","UP CENTRAL","UP WEST"};

    private ImageView imgBack;
    String state = "";

    private ArrayList<RsmModel> rsmModelArrayList = new ArrayList<RsmModel>();
    private ArrayList<RsmModel> rsmModelArrayList1 = new ArrayList<RsmModel>();
    Rsm1Adapter rsmAdapter;
    private ListView rsmList;

    ArrayList<AsmModel> asmModelArrayList = new ArrayList<AsmModel>();
    ListView asmList;
    AsmAdapter asmAdapter;

    private LinearLayout liTypeSelection;
    private TextView tvRsm, tvAsm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vp_report);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(VpReportActivity.this,Preferences.Id);
        userState = pref.getStringPreference(VpReportActivity.this,Preferences.User_State);

        vpDistributorAdapter = new VpDistributorAdapter(VpReportActivity.this, distributorModelArrayList1);
        distributorList = (ListView) findViewById(R.id.distributorList);
        distributorList.setAdapter(vpDistributorAdapter);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        getAllData(userId);

        distributorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VpStoreActivity.coming_from = "distributor";
                VpStoreActivity.state = distributorModelArrayList.get(i).getState();
                VpStoreActivity.distributor = distributorModelArrayList.get(i).getName();
                startActivity(new Intent(VpReportActivity.this, VpStoreActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        etKeyword = (EditText) findViewById(R.id.etKeyword);
        spinState = (Spinner) findViewById(R.id.spinState);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,states);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinState.setAdapter(aa);

        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state = states[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rsmList = (ListView) findViewById(R.id.rsmList);

        rsmAdapter = new Rsm1Adapter(VpReportActivity.this, rsmModelArrayList1);
        rsmList.setAdapter(rsmAdapter);

        getVpDataAll(userId,state);

        rsmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VpAsmActivity.state = state;
                VpAsmActivity.rsm = rsmModelArrayList.get(i).getName();
                startActivity(new Intent(VpReportActivity.this, VpAsmActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        asmAdapter = new AsmAdapter(VpReportActivity.this, asmModelArrayList);
        asmList = (ListView) findViewById(R.id.asmList);
        asmList.setAdapter(asmAdapter);

        asmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VpAseActivity.state = state;
                VpAseActivity.coming_from = "vp_report";
                VpAseActivity.asm = asmModelArrayList.get(i).getName();
                startActivity(new Intent(VpReportActivity.this, VpAseActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        liTypeSelection = (LinearLayout) findViewById(R.id.liTypeSelection);
        tvAsm = (TextView) findViewById(R.id.tvAsm);
        tvRsm = (TextView) findViewById(R.id.tvRsm);
        tvAsm.setOnClickListener(this);
        tvRsm.setOnClickListener(this);

        tvSearch = (TextView) findViewById(R.id.tvSearch);
        tvSearch.setOnClickListener(this);
    }

    private void getAllData(String userId){
        dialogView.showCustomSpinProgress(VpReportActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VP_REPORT_DISTRIBUTOR, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                GlobalVariable.vpDistributorModelArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("data");

                                if (dataArray.length()>0){
                                    for (int i = 0; i<dataArray.length();i++){
                                        JSONObject dataObj = dataArray.getJSONObject(i);
                                        VpDistributorModel distributorModel = new VpDistributorModel();
                                        distributorModel.setName(dataObj.getString("name"));
                                        distributorModel.setValue(dataObj.getString("value"));
                                        distributorModel.setState(dataObj.getString("state"));
                                        distributorModelArrayList.add(distributorModel);
                                        GlobalVariable.vpDistributorModelArrayList.add(distributorModel);
                                    }
                                }

                                distributorModelArrayList1 = distributorModelArrayList;
                                //GlobalVariable.vpDistributorModelArrayList = distributorModelArrayList1;
                                Log.d("distributor1 length>>",String.valueOf(distributorModelArrayList1.size()));
                                Log.d("distributor4 length>>",String.valueOf(GlobalVariable.vpDistributorModelArrayList.size()));
                                //vpDistributorAdapter.notifyDataSetChanged();
                                vpDistributorAdapter = new VpDistributorAdapter(VpReportActivity.this, distributorModelArrayList1);
                                distributorList.setAdapter(vpDistributorAdapter);

                            }else {
                                dialogView.showSingleButtonDialog(VpReportActivity.this, getResources().getString(R.string.app_name),
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

    private void getVpDataAll(String userId, String state){
        //dialogView.showCustomSpinProgress(VpReportActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("state", "");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VP_REPORT_ALL, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        //dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray rsmArray = response.getJSONArray("rsm");
                                JSONArray asmArray = response.getJSONArray("asm");

                                if (rsmArray.length()>0){
                                    for (int i = 0; i<rsmArray.length();i++){
                                        JSONObject rsmObj = rsmArray.getJSONObject(i);
                                        RsmModel rsmModel = new RsmModel();
                                        rsmModel.setName(rsmObj.getString("name"));
                                        rsmModel.setValue(rsmObj.getString("value"));
                                        rsmModelArrayList.add(rsmModel);
                                        GlobalVariable.rsmModelArrayList.add(rsmModel);
                                    }
                                }
                                rsmModelArrayList1 = rsmModelArrayList;
                                //rsmAdapter.notifyDataSetChanged();
                                rsmAdapter = new Rsm1Adapter(VpReportActivity.this, rsmModelArrayList1);
                                rsmList.setAdapter(rsmAdapter);

                                if (asmArray.length()>0){
                                    for (int i = 0; i<asmArray.length();i++){
                                        JSONObject dataObj = asmArray.getJSONObject(i);
                                        AsmModel asmModel = new AsmModel();
                                        asmModel.setName(dataObj.getString("name"));
                                        asmModel.setValue(dataObj.getString("value"));
                                        asmModelArrayList.add(asmModel);
                                    }
                                }
                                asmAdapter.notifyDataSetChanged();

                                //getRsmBarEntries();

                            }else {
                                dialogView.showSingleButtonDialog(VpReportActivity.this, getResources().getString(R.string.app_name),
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.tvSearch:
                searchDistributor();
                break;
            case R.id.tvRsm:
                liTypeSelection.setBackground(getResources().getDrawable(R.drawable.tab_left));
                tvRsm.setTextColor(getResources().getColor(R.color.red));
                tvAsm.setTextColor(getResources().getColor(R.color.black));
                rsmList.setVisibility(View.VISIBLE);
                asmList.setVisibility(View.GONE);
                break;
            case R.id.tvAsm:
                liTypeSelection.setBackground(getResources().getDrawable(R.drawable.tab_right));
                tvAsm.setTextColor(getResources().getColor(R.color.red));
                tvRsm.setTextColor(getResources().getColor(R.color.black));
                rsmList.setVisibility(View.GONE);
                asmList.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void searchDistributor(){
        distributorModelArrayList1.clear();
        String keyword = etKeyword.getText().toString();

        Log.d("state>>",state);
        Log.d("keyword>>",keyword);

        Log.d("distributor3 length>>",String.valueOf(GlobalVariable.vpDistributorModelArrayList.size()));

        if (!keyword.equals("") && !state.equals("")){
            for (int i=0;i<GlobalVariable.vpDistributorModelArrayList.size();i++){
                String distributor = GlobalVariable.vpDistributorModelArrayList.get(i).getName();
                String state1 = GlobalVariable.vpDistributorModelArrayList.get(i).getState();
                if (state1.equals(state) && distributor.toLowerCase().indexOf(keyword.toLowerCase())!=-1){
                    distributorModelArrayList1.add(GlobalVariable.vpDistributorModelArrayList.get(i));
                }
            }
            //vpDistributorAdapter.notifyDataSetChanged();
        }else if (!keyword.equals("") && state.equals("")){
            for (int i=0;i<GlobalVariable.vpDistributorModelArrayList.size();i++){
                String distributor = GlobalVariable.vpDistributorModelArrayList.get(i).getName();
                if (distributor.indexOf(keyword)!=-1){
                    distributorModelArrayList1.add(GlobalVariable.vpDistributorModelArrayList.get(i));
                }
            }
            //vpDistributorAdapter.notifyDataSetChanged();
        }else if (keyword.equals("") && !state.equals("")){
            for (int i=0;i<GlobalVariable.vpDistributorModelArrayList.size();i++){
                String state1 = GlobalVariable.vpDistributorModelArrayList.get(i).getState();
                if (state1.equals(state)){
                    distributorModelArrayList1.add(GlobalVariable.vpDistributorModelArrayList.get(i));
                }
            }
            //vpDistributorAdapter.notifyDataSetChanged();
        }else{
            distributorModelArrayList1 = GlobalVariable.vpDistributorModelArrayList;
            //vpDistributorAdapter.notifyDataSetChanged();
        }
        Log.d("distributor2 length>>",String.valueOf(distributorModelArrayList1.size()));
        vpDistributorAdapter = new VpDistributorAdapter(VpReportActivity.this, distributorModelArrayList1);
        distributorList.setAdapter(vpDistributorAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VpReportActivity.this, VpStartingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}