package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.b2bapp.onn.adapter.ActivityLogAdapter;
import com.b2bapp.onn.adapter.StoreVisitAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.ActivityLogModel;
import com.b2bapp.onn.model.StoreVisitModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StoreVisitActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private ListView logList;
    ArrayList<StoreVisitModel> storeVisitModelArrayList = new ArrayList<StoreVisitModel>();
    StoreVisitAdapter storeVisitAdapter;

    String ase = "";

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="StoreVisitActivity";
    Preferences pref;

    public static String storeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_visit);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        ase = pref.getStringPreference(StoreVisitActivity.this,Preferences.User_fname)
                + " "+ pref.getStringPreference(StoreVisitActivity.this,Preferences.User_lname);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        logList = (ListView) findViewById(R.id.logList);
        storeVisitAdapter = new StoreVisitAdapter(StoreVisitActivity.this,storeVisitModelArrayList);
        logList.setAdapter(storeVisitAdapter);

        //Calling getStoreVisitTask method
        getStoreVisitTask(storeId);
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
        StoreDetailsActivity.storeModel = GlobalVariable.storeModel;
        Intent intent = new Intent(StoreVisitActivity.this, StoreDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for store visit list
     * @param storeId
     */
    public void getStoreVisitTask(String storeId){
        dialogView.showCustomSpinProgress(StoreVisitActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_STORE_VISIT_LIST+"/"+storeId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+">>activity log", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray logsArray = response.getJSONArray("data");

                                if (logsArray.length() > 0) {
                                    for (int i = 0; i < logsArray.length(); i++) {
                                        JSONObject logObj = logsArray.getJSONObject(i);
                                        StoreVisitModel activityLogModel = new StoreVisitModel();

                                        activityLogModel.setId(logObj.getString("id"));
                                        activityLogModel.setDate(logObj.getString("date"));
                                        activityLogModel.setTime(logObj.getString("time"));
                                        activityLogModel.setSales_person(logObj.getJSONObject("users").getString("fname")
                                        +" "+logObj.getJSONObject("users").getString("lname"));

                                        storeVisitModelArrayList.add(activityLogModel);
                                    }
                                }

                                storeVisitAdapter.notifyDataSetChanged();
                            }else {
                                dialogView.showSingleButtonDialog(StoreVisitActivity.this, getResources().getString(R.string.app_name),
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
}