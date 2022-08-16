package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.DistributorMomListAdapter;
import com.b2bapp.onn.adapter.NotificationAdapter;
import com.b2bapp.onn.base.RecyclerItemClickListener;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.DistributorMomModel;
import com.b2bapp.onn.model.NotificationModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView notiRv;
    ImageView imgBack;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="NotificationActivity";
    Preferences pref;

    String userId = "";

    List<NotificationModel> notiList = new ArrayList<>();

    NotificationAdapter notificationAdapter;
    int count = 0;

    public static String isfrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        notiRv = findViewById(R.id.notiRv);
        imgBack = findViewById(R.id.imgBack);

        userId = pref.getStringPreference(NotificationActivity.this, Preferences.Id);

        getNotificationList();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        notiRv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), notiRv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                callReadNotification(notiList.get(position).getId());

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    private void callReadNotification(String id) {
        dialogView.showCustomSpinProgress(NotificationActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("id", id);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_NOTIFICATION_READ, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {

                                getNotificationList();

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

    private void getNotificationList() {
        dialogView.showCustomSpinProgress(NotificationActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_NOTIFICATION_LIST, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        notiList.clear();
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {

                                JSONArray jsonArray = response.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++){

                                    NotificationModel nm = new NotificationModel();
                                    nm.setId(jsonArray.getJSONObject(i).getString("id"));
                                    nm.setSender(jsonArray.getJSONObject(i).getString("sender"));
                                    nm.setReceiver(jsonArray.getJSONObject(i).getString("receiver"));
                                    nm.setType(jsonArray.getJSONObject(i).getString("type"));
                                    nm.setRoute(jsonArray.getJSONObject(i).getString("route"));
                                    nm.setTitle(jsonArray.getJSONObject(i).getString("title"));
                                    nm.setBody(jsonArray.getJSONObject(i).getString("body"));
                                    if (jsonArray.getJSONObject(i).getString("read_flag").equals("0")){
                                        count = count + 1;
                                    }
                                    nm.setRead_flag(jsonArray.getJSONObject(i).getString("read_flag"));
                                    //nm.setRead_at(jsonArray.getJSONObject(i).getString("read_at"));
                                    nm.setCreated_at(jsonArray.getJSONObject(i).getString("created_at"));
                                    nm.setUpdated_at(jsonArray.getJSONObject(i).getString("updated_at"));

                                    notiList.add(nm);

                                }

                                if (notiList.size() > 0){
                                    notificationAdapter = new NotificationAdapter(NotificationActivity.this, notiList);
                                    notiRv.setLayoutManager(new LinearLayoutManager(
                                            NotificationActivity.this,
                                            LinearLayoutManager.VERTICAL,
                                            false
                                    ));

                                    notiRv.setAdapter(notificationAdapter);
                                }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isfrom.equals("asm")){
            startActivity(new Intent(NotificationActivity.this, AsmDashboardActivity.class));
            finish();
        }else if (isfrom.equals("vp")){
            startActivity(new Intent(NotificationActivity.this, VpStartingActivity.class));
            finish();
        }else if (isfrom.equals("rsm")){
            startActivity(new Intent(NotificationActivity.this, RsmDashboardActivity.class));
            finish();
        }
    }
}