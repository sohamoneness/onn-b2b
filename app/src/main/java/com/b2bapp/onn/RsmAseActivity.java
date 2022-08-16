package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.b2bapp.onn.adapter.AseAdapter;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.AseModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RsmAseActivity extends AppCompatActivity implements View.OnClickListener {

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="VpAseActivity";
    Preferences pref;

    public static String state = "";
    public static String asm = "";

    ArrayList<AseModel> asmModelArrayList = new ArrayList<AseModel>();
    ListView asmList;
    AseAdapter asmAdapter;

    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsm_ase);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        asmAdapter = new AseAdapter(RsmAseActivity.this, asmModelArrayList);
        asmList = (ListView) findViewById(R.id.asmList);
        asmList.setAdapter(asmAdapter);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        getAllData(state,asm);

        asmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RsmStoreActivity.coming_from = "ase";
                RsmStoreActivity.state = state;
                RsmStoreActivity.ase = asmModelArrayList.get(i).getName();
                startActivity(new Intent(RsmAseActivity.this, RsmStoreActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    private void getAllData(String state, String asm){
        dialogView.showCustomSpinProgress(RsmAseActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("asm", asm);
        postParam.put("state", state);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_VP_REPORT_ASM_WISE, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray dataArray = response.getJSONArray("data");

                                if (dataArray.length()>0){
                                    for (int i = 0; i<dataArray.length();i++){
                                        JSONObject dataObj = dataArray.getJSONObject(i);
                                        AseModel asmModel = new AseModel();
                                        asmModel.setName(dataObj.getString("name"));
                                        asmModel.setValue(dataObj.getString("value"));
                                        asmModelArrayList.add(asmModel);
                                    }
                                }
                                asmAdapter.notifyDataSetChanged();

                            }else {
                                dialogView.showSingleButtonDialog(RsmAseActivity.this, getResources().getString(R.string.app_name),
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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RsmAseActivity.this, RsmWiseAsmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}