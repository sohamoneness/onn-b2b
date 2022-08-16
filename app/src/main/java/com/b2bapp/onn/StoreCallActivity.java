package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.StoreCallAdapter;
import com.b2bapp.onn.adapter.StorelistAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreCallActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView storeList;
    private ImageView imgBack, imgAdd, imgSearch;

    ArrayList<StoreModel> storeModelArrayList = new ArrayList<StoreModel>();
    StoreCallAdapter storeCallAdapter;

    String ase = "";

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="StoreCallActivity";
    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_call);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        ase = pref.getStringPreference(StoreCallActivity.this,Preferences.User_fname)
                + " "+ pref.getStringPreference(StoreCallActivity.this,Preferences.User_lname);


        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        imgAdd = (ImageView) findViewById(R.id.imgAdd);

        imgBack.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
        imgAdd.setOnClickListener(this);

        storeList = (ListView) findViewById(R.id.storeList);

        //Checking store list is present on cache or not
//        if (GlobalVariable.storeModelArrayList.size()==0){
//            //Calling getStoreListTask method
//            getStoreListTask();
//        }else{
//            storeCallAdapter = new StoreCallAdapter(StoreCallActivity.this, GlobalVariable.storeModelArrayList);
//            storeList.setAdapter(storeCallAdapter);
//        }
        //Calling getStoreListTask method
        getStoreListTask();

        storeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StoreDetailsActivity.storeModel = GlobalVariable.storeModelArrayList.get(i);
                GlobalVariable.storeModel = GlobalVariable.storeModelArrayList.get(i);
                startActivity(new Intent(StoreCallActivity.this, StoreDetailsActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStoreListTask(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.imgSearch:
                startActivity(new Intent(StoreCallActivity.this, StoreCallSearchActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.imgAdd:
                startActivity(new Intent(StoreCallActivity.this, StoreAddActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StoreCallActivity.this, TaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for getting store list
     */
    public void getStoreListTask(){
        dialogView.showCustomSpinProgress(StoreCallActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_STORE_LIST+"?ase="+ase+"&area="+GlobalVariable.selected_area, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                storeModelArrayList.clear();
                                JSONArray storesArray = response.getJSONArray("data");

                                if (storesArray.length() > 0) {
                                    for (int i = 0; i < storesArray.length(); i++) {
                                        JSONObject storeObj = storesArray.getJSONObject(i);
                                        StoreModel storeModel = new StoreModel();

                                        storeModel.setId(storeObj.getString("id"));
                                        storeModel.setStore_name(storeObj.getString("store_name"));
                                        storeModel.setBussiness_name(storeObj.getString("bussiness_name"));
                                        storeModel.setStore_OCC_number(storeObj.getString("store_OCC_number"));
                                        storeModel.setContact(storeObj.getString("contact"));
                                        storeModel.setEmail(storeObj.getString("email"));
                                        storeModel.setWhatsapp(storeObj.getString("whatsapp"));
                                        storeModel.setAddress(storeObj.getString("address"));
                                        storeModel.setArea(storeObj.getString("area"));
                                        storeModel.setState(storeObj.getString("state"));
                                        storeModel.setCity(storeObj.getString("city"));
                                        storeModel.setPin(storeObj.getString("pin"));
                                        storeModel.setImage(storeObj.getString("image"));

                                        storeModelArrayList.add(storeModel);
                                    }
                                }

                                GlobalVariable.storeModelArrayList = storeModelArrayList;

                                storeCallAdapter = new StoreCallAdapter(StoreCallActivity.this, GlobalVariable.storeModelArrayList);
                                storeList.setAdapter(storeCallAdapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
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