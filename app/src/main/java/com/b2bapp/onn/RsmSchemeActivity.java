package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.SchemeAdapter;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.customView.ExpandableHeightGridView;
import com.b2bapp.onn.model.SchemeModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RsmSchemeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private LinearLayout liSchemeSelection;
    private TextView tvCurrent, tvOld;
    private ExpandableHeightGridView gvSchemes;

    ArrayList<SchemeModel> oldSchemeModelArrayList = new ArrayList<SchemeModel>();
    ArrayList<SchemeModel> currentSchemeModelArrayList = new ArrayList<SchemeModel>();
    SchemeAdapter schemeAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="SchemeActivity";
    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsm_scheme);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        liSchemeSelection = (LinearLayout) findViewById(R.id.liSchemeSelection);
        tvCurrent = (TextView) findViewById(R.id.tvCurrent);
        tvOld = (TextView) findViewById(R.id.tvOld);

        imgBack.setOnClickListener(this);
        tvCurrent.setOnClickListener(this);
        tvOld.setOnClickListener(this);

        gvSchemes = (ExpandableHeightGridView) findViewById(R.id.gvSchemes);
        schemeAdapter = new SchemeAdapter(RsmSchemeActivity.this,currentSchemeModelArrayList);
        gvSchemes.setAdapter(schemeAdapter);

        gvSchemes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //LoadWebviewActivity.url = WebServices.IMAGE_BASE_URL+currentSchemeModelArrayList.get(i).getPdf();
//                startActivity(new Intent(SchemeActivity.this, LoadWebviewActivity.class));
//                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//                finish();

                String url = WebServices.IMAGE_BASE_URL+currentSchemeModelArrayList.get(i).getPdf();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        //Calling getSchemesTask method
        getSchemesTask();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.tvCurrent:
                liSchemeSelection.setBackground(getResources().getDrawable(R.drawable.tab_left));
                tvCurrent.setTextColor(getResources().getColor(R.color.red));
                tvOld.setTextColor(getResources().getColor(R.color.black));
                schemeAdapter = new SchemeAdapter(RsmSchemeActivity.this,currentSchemeModelArrayList);
                gvSchemes.setAdapter(schemeAdapter);
                break;
            case R.id.tvOld:
                liSchemeSelection.setBackground(getResources().getDrawable(R.drawable.tab_right));
                tvCurrent.setTextColor(getResources().getColor(R.color.black));
                tvOld.setTextColor(getResources().getColor(R.color.red));
                schemeAdapter = new SchemeAdapter(RsmSchemeActivity.this,oldSchemeModelArrayList);
                gvSchemes.setAdapter(schemeAdapter);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RsmSchemeActivity.this, RsmDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for getting scheme list
     */
    public void getSchemesTask(){
        dialogView.showCustomSpinProgress(RsmSchemeActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_OFFER_LIST, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray offersArray = response.getJSONArray("data");

                                if (offersArray.length() > 0) {
                                    for (int i = 0; i < offersArray.length(); i++) {
                                        JSONObject offerObj = offersArray.getJSONObject(i);
                                        SchemeModel schemeModel = new SchemeModel();

                                        schemeModel.setId(offerObj.getString("id"));
                                        schemeModel.setImage(offerObj.getString("image"));
                                        schemeModel.setPdf(offerObj.getString("pdf"));
                                        schemeModel.setTitle(offerObj.getString("title"));
                                        schemeModel.setIs_current(offerObj.getString("is_current"));

                                        if (schemeModel.getIs_current().equals("1")){
                                            currentSchemeModelArrayList.add(schemeModel);
                                        }else{
                                            oldSchemeModelArrayList.add(schemeModel);
                                        }
                                    }
                                }

                                schemeAdapter.notifyDataSetChanged();
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