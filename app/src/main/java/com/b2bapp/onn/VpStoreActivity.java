package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.b2bapp.onn.adapter.AseAdapter;
import com.b2bapp.onn.adapter.VpStoreAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.AseModel;
import com.b2bapp.onn.model.VpStoreModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VpStoreActivity extends AppCompatActivity implements View.OnClickListener {

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="VpAseActivity";
    Preferences pref;

    public static String state = "";
    public static String distributor = "";
    public static String ase = "";
    public static String coming_from = "";

    ArrayList<VpStoreModel> asmModelArrayList = new ArrayList<VpStoreModel>();
    ListView asmList;
    VpStoreAdapter asmAdapter;

    private ImageView imgBack;

    private EditText etKeyword;
    private TextView tvSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vp_store);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        asmAdapter = new VpStoreAdapter(VpStoreActivity.this, asmModelArrayList);
        asmList = (ListView) findViewById(R.id.asmList);
        asmList.setAdapter(asmAdapter);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        if (coming_from.equals("distributor")){
            getAllData(state,distributor);
        }else{
            getAllDataAseWise(state,ase);
        }


        asmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VpStoreOrdersActivity.storeId = asmModelArrayList.get(i).getStore_id();
                startActivity(new Intent(VpStoreActivity.this, VpStoreOrdersActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        etKeyword = (EditText) findViewById(R.id.etKeyword);
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        tvSearch.setOnClickListener(this);
    }

    private void getAllData(String state, String distributor){
        dialogView.showCustomSpinProgress(VpStoreActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("distributor", distributor);
        postParam.put("state", state);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VP_REPORT_DISTRIBUTOR_WISE, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                GlobalVariable.vpStoreModelArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("data");

                                if (dataArray.length()>0){
                                    for (int i = 0; i<dataArray.length();i++){
                                        JSONObject dataObj = dataArray.getJSONObject(i);
                                        VpStoreModel asmModel = new VpStoreModel();
                                        asmModel.setName(dataObj.getString("name"));
                                        asmModel.setStore_id(dataObj.getString("store_id"));
                                        asmModel.setValue(dataObj.getString("value"));
                                        asmModelArrayList.add(asmModel);
                                        GlobalVariable.vpStoreModelArrayList.add(asmModel);
                                    }
                                }
                                //asmAdapter.notifyDataSetChanged();
                                asmAdapter = new VpStoreAdapter(VpStoreActivity.this, asmModelArrayList);
                                asmList.setAdapter(asmAdapter);

                            }else {
                                dialogView.showSingleButtonDialog(VpStoreActivity.this, getResources().getString(R.string.app_name),
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

    private void getAllDataAseWise(String state, String ase){
        dialogView.showCustomSpinProgress(VpStoreActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("ase", ase);
        postParam.put("state", "");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VP_REPORT_ASE_WISE, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                GlobalVariable.vpStoreModelArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("data");

                                if (dataArray.length()>0){
                                    for (int i = 0; i<dataArray.length();i++){
                                        JSONObject dataObj = dataArray.getJSONObject(i);
                                        VpStoreModel asmModel = new VpStoreModel();
                                        asmModel.setName(dataObj.getString("name"));
                                        asmModel.setStore_id(dataObj.getString("store_id"));
                                        asmModel.setValue(dataObj.getString("value"));
                                        asmModelArrayList.add(asmModel);
                                        GlobalVariable.vpStoreModelArrayList.add(asmModel);
                                    }
                                }
                                //asmAdapter.notifyDataSetChanged();
                                asmAdapter = new VpStoreAdapter(VpStoreActivity.this, asmModelArrayList);
                                asmList.setAdapter(asmAdapter);

                            }else {
                                dialogView.showSingleButtonDialog(VpStoreActivity.this, getResources().getString(R.string.app_name),
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
                asmModelArrayList.clear();
                String keyword = etKeyword.getText().toString();
                for (int i=0;i<GlobalVariable.vpStoreModelArrayList.size();i++){
                    String distributor = GlobalVariable.vpStoreModelArrayList.get(i).getName();
                    if (distributor.toLowerCase().indexOf(keyword.toLowerCase())!=-1){
                        asmModelArrayList.add(GlobalVariable.vpStoreModelArrayList.get(i));
                    }
                }
                asmAdapter = new VpStoreAdapter(VpStoreActivity.this, asmModelArrayList);
                asmList.setAdapter(asmAdapter);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (coming_from.equals("distributor")){
            super.onBackPressed();
            Intent intent = new Intent(VpStoreActivity.this, VpReportActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            super.onBackPressed();
            Intent intent = new Intent(VpStoreActivity.this, VpAseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }
}