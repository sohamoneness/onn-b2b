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
import com.b2bapp.onn.adapter.AseSalesReportAdapter;
import com.b2bapp.onn.adapter.PrimarySalesReportAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.AseSalesModel;
import com.b2bapp.onn.model.PrimarySalesModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RsmAsmWiseTeamReportActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private LinearLayout liTypeSelection, liPrimary, liSecondary;
    private ListView primaryList, secondaryList;
    private TextView tvPrimary, tvSecondary;

    ArrayList<PrimarySalesModel> primarySalesModelArrayList = new ArrayList<PrimarySalesModel>();
    ArrayList<AseSalesModel> aseSalesModelArrayList = new ArrayList<AseSalesModel>();
    private PrimarySalesReportAdapter primarySalesReportAdapter;
    private AseSalesReportAdapter aseSalesReportAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="AsmWiseTeamReportResultActivity";
    Preferences pref;

    String asm = "";

    public static String coming_from = "";

    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsm_asm_wise_team_report);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        asm = pref.getStringPreference(RsmAsmWiseTeamReportActivity.this,Preferences.User_fname)
                + " "+ pref.getStringPreference(RsmAsmWiseTeamReportActivity.this,Preferences.User_lname);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        liTypeSelection = (LinearLayout) findViewById(R.id.liTypeSelection);
        liPrimary = (LinearLayout) findViewById(R.id.liPrimary);
        liSecondary = (LinearLayout) findViewById(R.id.liSecondary);

        tvPrimary = (TextView) findViewById(R.id.tvPrimary);
        tvSecondary = (TextView) findViewById(R.id.tvSecondary);
        tvPrimary.setOnClickListener(this);
        tvSecondary.setOnClickListener(this);

        primaryList = (ListView) findViewById(R.id.primaryList);
        secondaryList = (ListView) findViewById(R.id.secondaryList);

        primarySalesReportAdapter = new PrimarySalesReportAdapter(RsmAsmWiseTeamReportActivity.this,primarySalesModelArrayList);
        primaryList.setAdapter(primarySalesReportAdapter);

        aseSalesReportAdapter = new AseSalesReportAdapter(RsmAsmWiseTeamReportActivity.this,aseSalesModelArrayList);
        secondaryList.setAdapter(aseSalesReportAdapter);

        primaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AseDistributorOrderListActivity.coming_from = "rsm_store_wise_report";
                AseDistributorOrderListActivity.userId = primarySalesModelArrayList.get(i).getDistributor_id();
                startActivity(new Intent(RsmAsmWiseTeamReportActivity.this, AseDistributorOrderListActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        secondaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalVariable.asm_ase_name = aseSalesModelArrayList.get(i).getAse_name();
                GlobalVariable.store_report_ase_id = aseSalesModelArrayList.get(i).getAse_id();
                GlobalVariable.store_report_date_from = GlobalVariable.asm_report_date_from;
                GlobalVariable.store_report_date_to = GlobalVariable.asm_report_date_to;
                GlobalVariable.store_report_collection = GlobalVariable.asm_report_collection;
                GlobalVariable.store_report_style_no = GlobalVariable.asm_report_style_no;
                AsmAseWiseTeamReportResultActivity.coming_from = "rsm";
                startActivity(new Intent(RsmAsmWiseTeamReportActivity.this, AsmAseWiseTeamReportResultActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(GlobalVariable.rsm_asm_name);

        getAllData();
    }

    private void getAllData(){
        dialogView.showCustomSpinProgress(RsmAsmWiseTeamReportActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("asm", GlobalVariable.rsm_asm_name);
        postParam.put("date_from", GlobalVariable.asm_report_date_from);
        postParam.put("date_to", GlobalVariable.asm_report_date_to);
        postParam.put("collection", GlobalVariable.asm_report_collection);
        postParam.put("style_no", GlobalVariable.asm_report_style_no);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_ASM_WISE_TEAM_REPORT, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                primarySalesModelArrayList.clear();
                                aseSalesModelArrayList.clear();
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

                                JSONArray dataArray1 = response.getJSONArray("data").getJSONObject(0).getJSONArray("secondary_sales");

                                if (dataArray1.length()>0){
                                    for (int i = 0; i<dataArray1.length();i++){
                                        JSONObject dataObj = dataArray1.getJSONObject(i);
                                        AseSalesModel dataModel1 = new AseSalesModel();
                                        dataModel1.setAse_id(dataObj.getString("ase_id"));
                                        dataModel1.setAse_name(dataObj.getString("ase_name"));
                                        dataModel1.setQuantity(dataObj.getString("quantity"));
                                        aseSalesModelArrayList.add(dataModel1);
                                    }
                                }
                                aseSalesReportAdapter.notifyDataSetChanged();

                            }else {
                                dialogView.showSingleButtonDialog(RsmAsmWiseTeamReportActivity.this, getResources().getString(R.string.app_name),
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
                primaryList.setVisibility(View.VISIBLE);
                secondaryList.setVisibility(View.GONE);
                break;
            case R.id.tvSecondary:
                liTypeSelection.setBackground(getResources().getDrawable(R.drawable.tab_right));
                tvSecondary.setTextColor(getResources().getColor(R.color.red));
                tvPrimary.setTextColor(getResources().getColor(R.color.black));
                liPrimary.setVisibility(View.GONE);
                liSecondary.setVisibility(View.VISIBLE);
                primaryList.setVisibility(View.GONE);
                secondaryList.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (coming_from.equals("vp")){
            super.onBackPressed();
            Intent intent = new Intent(RsmAsmWiseTeamReportActivity.this, VpTeamWiseReportResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if (coming_from.equals("vp1")){
            super.onBackPressed();
            Intent intent = new Intent(RsmAsmWiseTeamReportActivity.this, VpRsmWiseReportActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            super.onBackPressed();
            Intent intent = new Intent(RsmAsmWiseTeamReportActivity.this, RsmTeamWiseReportResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }
}