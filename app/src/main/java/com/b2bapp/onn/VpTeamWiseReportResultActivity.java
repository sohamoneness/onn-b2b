package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.AsmSalesAdapter;
import com.b2bapp.onn.adapter.PrimarySalesReportAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.AsmSalesModel;
import com.b2bapp.onn.model.PrimarySalesModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VpTeamWiseReportResultActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private LinearLayout liTypeSelection, liPrimary, liSecondary,liSecondary1;
    private ListView primaryList, secondaryList,secondaryList1;
    private TextView tvPrimary, tvSecondary;

    ArrayList<PrimarySalesModel> primarySalesModelArrayList = new ArrayList<PrimarySalesModel>();
    ArrayList<AsmSalesModel> asmSalesModelArrayList = new ArrayList<AsmSalesModel>();
    ArrayList<AsmSalesModel> rsmSalesModelArrayList = new ArrayList<AsmSalesModel>();
    private PrimarySalesReportAdapter primarySalesReportAdapter;
    private AsmSalesAdapter asmSalesAdapter;
    private AsmSalesAdapter rsmSalesAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="RsmTeamWiseReportResultActivity";
    Preferences pref;

    String vp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vp_team_wise_report_result);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        vp = pref.getStringPreference(VpTeamWiseReportResultActivity.this,Preferences.User_fname)
                + " "+ pref.getStringPreference(VpTeamWiseReportResultActivity.this,Preferences.User_lname);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        liTypeSelection = (LinearLayout) findViewById(R.id.liTypeSelection);
        liPrimary = (LinearLayout) findViewById(R.id.liPrimary);
        liSecondary = (LinearLayout) findViewById(R.id.liSecondary);
        liSecondary1 = (LinearLayout) findViewById(R.id.liSecondary1);

        tvPrimary = (TextView) findViewById(R.id.tvPrimary);
        tvSecondary = (TextView) findViewById(R.id.tvSecondary);
        tvPrimary.setOnClickListener(this);
        tvSecondary.setOnClickListener(this);

        primaryList = (ListView) findViewById(R.id.primaryList);
        secondaryList = (ListView) findViewById(R.id.secondaryList);
        secondaryList1 = (ListView) findViewById(R.id.secondaryList1);

        primarySalesReportAdapter = new PrimarySalesReportAdapter(VpTeamWiseReportResultActivity.this,primarySalesModelArrayList);
        primaryList.setAdapter(primarySalesReportAdapter);

        asmSalesAdapter = new AsmSalesAdapter(VpTeamWiseReportResultActivity.this,asmSalesModelArrayList);
        secondaryList1.setAdapter(asmSalesAdapter);

        rsmSalesAdapter = new AsmSalesAdapter(VpTeamWiseReportResultActivity.this,rsmSalesModelArrayList);
        secondaryList.setAdapter(rsmSalesAdapter);

        primaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AseDistributorOrderListActivity.coming_from = "vp_store_wise_report";
                AseDistributorOrderListActivity.userId = primarySalesModelArrayList.get(i).getDistributor_id();
                startActivity(new Intent(VpTeamWiseReportResultActivity.this, AseDistributorOrderListActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        secondaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalVariable.vp_rsm_name = rsmSalesModelArrayList.get(i).getAsm_name();
                GlobalVariable.rsm_report_date_from = GlobalVariable.vp_report_date_from;
                GlobalVariable.rsm_report_date_to = GlobalVariable.vp_report_date_to;
                GlobalVariable.rsm_report_collection = GlobalVariable.vp_report_collection;
                GlobalVariable.rsm_report_style_no = GlobalVariable.vp_report_style_no;
                startActivity(new Intent(VpTeamWiseReportResultActivity.this, VpRsmWiseReportActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        secondaryList1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalVariable.rsm_asm_name = asmSalesModelArrayList.get(i).getAsm_name();
                GlobalVariable.asm_report_date_from = GlobalVariable.vp_report_date_from;
                GlobalVariable.asm_report_date_to = GlobalVariable.vp_report_date_to;
                GlobalVariable.asm_report_collection = GlobalVariable.vp_report_collection;
                GlobalVariable.asm_report_style_no = GlobalVariable.vp_report_style_no;
                RsmAsmWiseTeamReportActivity.coming_from = "vp";
                startActivity(new Intent(VpTeamWiseReportResultActivity.this, RsmAsmWiseTeamReportActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        getAllData();
    }

    private void getAllData(){
        dialogView.showCustomSpinProgress(VpTeamWiseReportResultActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("vp", vp);
        postParam.put("date_from", GlobalVariable.vp_report_date_from);
        postParam.put("date_to", GlobalVariable.vp_report_date_to);
        postParam.put("state", GlobalVariable.vp_report_state);
        postParam.put("area", GlobalVariable.vp_report_area);
        postParam.put("collection", GlobalVariable.vp_report_collection);
        postParam.put("style_no", GlobalVariable.vp_report_style_no);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VP_WISE_TEAM_REPORT, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                primarySalesModelArrayList.clear();
                                asmSalesModelArrayList.clear();
                                rsmSalesModelArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("data").getJSONObject(0).getJSONArray("primary_sales");

                                if (dataArray.length()>0){
                                    for (int i = 0; i<dataArray.length();i++){
                                        JSONObject dataObj = dataArray.getJSONObject(i);
                                        PrimarySalesModel dataModel = new PrimarySalesModel();
                                        dataModel.setDistributor_id(dataObj.getString("distributor_id"));
                                        dataModel.setDistributor_name(dataObj.getString("distributor_name"));
                                        dataModel.setQuantity(dataObj.getString("quantity"));
                                        dataModel.setAmount("0");
                                        primarySalesModelArrayList.add(dataModel);
                                    }
                                }
                                primarySalesReportAdapter.notifyDataSetChanged();

                                JSONArray dataArray1 = response.getJSONArray("data").getJSONObject(0).getJSONArray("secondary_sales_asm");

                                if (dataArray1.length()>0){
                                    for (int i = 0; i<dataArray1.length();i++){
                                        JSONObject dataObj = dataArray1.getJSONObject(i);
                                        AsmSalesModel dataModel1 = new AsmSalesModel();
                                        dataModel1.setAsm_name(dataObj.getString("asm"));
                                        dataModel1.setQuantity(dataObj.getString("quantity"));
                                        asmSalesModelArrayList.add(dataModel1);
                                    }
                                }
                                asmSalesAdapter.notifyDataSetChanged();

                                JSONArray dataArray2 = response.getJSONArray("data").getJSONObject(0).getJSONArray("secondary_sales_rsm");

                                if (dataArray2.length()>0){
                                    for (int j = 0; j<dataArray2.length();j++){
                                        JSONObject dataObj = dataArray2.getJSONObject(j);
                                        AsmSalesModel dataModel2 = new AsmSalesModel();
                                        dataModel2.setAsm_name(dataObj.getString("rsm"));
                                        dataModel2.setQuantity(dataObj.getString("quantity"));
                                        rsmSalesModelArrayList.add(dataModel2);
                                    }
                                }
                                rsmSalesAdapter.notifyDataSetChanged();

                            }else {
                                dialogView.showSingleButtonDialog(VpTeamWiseReportResultActivity.this, getResources().getString(R.string.app_name),
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
            case R.id.tvPrimary:
                liTypeSelection.setBackground(getResources().getDrawable(R.drawable.tab_left));
                tvPrimary.setTextColor(getResources().getColor(R.color.red));
                tvSecondary.setTextColor(getResources().getColor(R.color.black));
                liPrimary.setVisibility(View.VISIBLE);
                liSecondary.setVisibility(View.GONE);
                liSecondary1.setVisibility(View.GONE);
                primaryList.setVisibility(View.VISIBLE);
                secondaryList.setVisibility(View.GONE);
                secondaryList1.setVisibility(View.GONE);
                break;
            case R.id.tvSecondary:
                liTypeSelection.setBackground(getResources().getDrawable(R.drawable.tab_right));
                tvSecondary.setTextColor(getResources().getColor(R.color.red));
                tvPrimary.setTextColor(getResources().getColor(R.color.black));
                liPrimary.setVisibility(View.GONE);
                liSecondary.setVisibility(View.VISIBLE);
                liSecondary1.setVisibility(View.VISIBLE);
                primaryList.setVisibility(View.GONE);
                secondaryList.setVisibility(View.VISIBLE);
                secondaryList1.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VpTeamWiseReportResultActivity.this, VpTeamWiseReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}