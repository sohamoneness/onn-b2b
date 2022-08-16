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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.DistributorListAdapter;
import com.b2bapp.onn.adapter.StoreCallAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.db.DbHelper;
import com.b2bapp.onn.model.DistributorModel;
import com.b2bapp.onn.model.MomDbModel;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.ConnectionStatus;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DistributorActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView distributorList;
    private ImageView imgBack, imgAdd, imgSearch,imgSync;

    ArrayList<DistributorModel> distributorModelArrayList = new ArrayList<DistributorModel>();
    DistributorListAdapter distributorListAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="DistributorActivity";
    Preferences pref;

    String ase = "";

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        dbHelper = new DbHelper(DistributorActivity.this);

        ase = pref.getStringPreference(DistributorActivity.this,Preferences.User_fname)
                + " "+ pref.getStringPreference(DistributorActivity.this,Preferences.User_lname);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        imgSync = (ImageView) findViewById(R.id.imgSync);

        imgBack.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgSync.setOnClickListener(this);

        distributorList = (ListView) findViewById(R.id.distributorList);

        //Checking store list is present on cache or not
//        if (GlobalVariable.distributorModelArrayList.size()==0){
//            //Calling getDistributorListTask method
//            getDistributorListTask();
//        }else{
//            distributorListAdapter = new DistributorListAdapter(DistributorActivity.this, GlobalVariable.distributorModelArrayList);
//            distributorList.setAdapter(distributorListAdapter);
//        }
        //Calling getDistributorListTask method
        //getDistributorListTask();
        if (ConnectionStatus.checkConnectionStatus(DistributorActivity.this)){
            //Calling getDistributorListTask method
            getDistributorListTask();
        }else{
            Log.d("distributor>>","no internet");
            distributorModelArrayList = dbHelper.fetchDistributorList();
            GlobalVariable.distributorModelArrayList = distributorModelArrayList;

            distributorListAdapter = new DistributorListAdapter(DistributorActivity.this, GlobalVariable.distributorModelArrayList);
            distributorList.setAdapter(distributorListAdapter);
        }

        distributorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
                startActivity(new Intent(DistributorActivity.this, DistributorSearchActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.imgAdd:
                startActivity(new Intent(DistributorActivity.this, DistributorAddActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.imgSync:
                if(ConnectionStatus.checkConnectionStatus(DistributorActivity.this)){
                    syncMomData();
                }else{
                    CToast.show(DistributorActivity.this,"You need internet connection for this");
                }
                break;
        }
    }

    public void syncMomData(){
        ArrayList<MomDbModel> momDbModelArrayList = new ArrayList<MomDbModel>();
        momDbModelArrayList = dbHelper.momLists();

        for (int i=0;i<momDbModelArrayList.size();i++){
            Log.d("id>>",momDbModelArrayList.get(i).getId());

            syncMomDataTask(momDbModelArrayList.get(i).getUser_id(),momDbModelArrayList.get(i).getDistributor_name(),
                    momDbModelArrayList.get(i).getComment());
        }

        dbHelper.deleteAllMOM();
        CToast.show(DistributorActivity.this,"All the local data has been successfully synced with server");
        startActivity(new Intent(DistributorActivity.this, TaskActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    public void syncMomDataTask(String user_id,String distributor_name, String comment){
        //dialogView.showCustomSpinProgress(DistributorActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", user_id);
        postParam.put("distributor_name", distributor_name);
        postParam.put("comment", comment);

        Log.d("order params>>",postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_STORE_DISTRIBUTOR_MOM, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        //dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                Log.d("Success","MOM data synced");
                            }else {
                                Log.d("Error","Some error occurred");
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DistributorActivity.this, TaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void getDistributorListTask(){
        dialogView.showCustomSpinProgress(DistributorActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_DISTRIBUTOR_LIST+"?ase="+ase+"&area="+GlobalVariable.selected_area, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray distributorsArray = response.getJSONArray("data");

                                if (distributorsArray.length() > 0) {
                                    for (int i = 0; i < distributorsArray.length(); i++) {
                                        JSONObject distributorObj = distributorsArray.getJSONObject(i);
                                        DistributorModel distributorModel = new DistributorModel();

                                        distributorModel.setId(distributorObj.getString("id"));
                                        distributorModel.setStore_name(distributorObj.getString("store_name"));
                                        distributorModel.setBussiness_name(distributorObj.getString("bussiness_name"));
                                        distributorModel.setStore_OCC_number(distributorObj.getString("store_OCC_number"));
                                        distributorModel.setContact(distributorObj.getString("contact"));
                                        distributorModel.setEmail(distributorObj.getString("email"));
                                        distributorModel.setWhatsapp(distributorObj.getString("whatsapp"));
                                        distributorModel.setAddress(distributorObj.getString("address"));
                                        distributorModel.setArea(distributorObj.getString("area"));
                                        distributorModel.setState(distributorObj.getString("state"));
                                        distributorModel.setCity(distributorObj.getString("city"));
                                        distributorModel.setPin(distributorObj.getString("pin"));
                                        distributorModel.setImage(distributorObj.getString("image"));
                                        distributorModel.setDistributor_id(distributorObj.getString("distributor_id"));

                                        distributorModelArrayList.add(distributorModel);
                                    }
                                }

                                GlobalVariable.distributorModelArrayList = distributorModelArrayList;

                                distributorListAdapter = new DistributorListAdapter(DistributorActivity.this, GlobalVariable.distributorModelArrayList);
                                distributorList.setAdapter(distributorListAdapter);
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