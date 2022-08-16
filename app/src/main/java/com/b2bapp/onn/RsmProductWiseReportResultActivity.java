package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.ProductWiseSalesAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.ProductWiseSalesModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RsmProductWiseReportResultActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private ListView secondaryList;

    private ArrayList<ProductWiseSalesModel> productWiseSalesModelArrayList = new ArrayList<ProductWiseSalesModel>();
    private ProductWiseSalesAdapter productWiseSalesAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="AsmProductWiseReportResultActivity";
    Preferences pref;

    String rsm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsm_product_wise_report_result);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        rsm = pref.getStringPreference(RsmProductWiseReportResultActivity.this,Preferences.User_fname)
                + " "+ pref.getStringPreference(RsmProductWiseReportResultActivity.this,Preferences.User_lname);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        secondaryList = (ListView) findViewById(R.id.secondaryList);

        productWiseSalesAdapter = new ProductWiseSalesAdapter(RsmProductWiseReportResultActivity.this,productWiseSalesModelArrayList);
        secondaryList.setAdapter(productWiseSalesAdapter);

        getAllData();
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
        Intent intent = new Intent(RsmProductWiseReportResultActivity.this, RsmProductWiseReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getAllData(){
        dialogView.showCustomSpinProgress(RsmProductWiseReportResultActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("rsm", rsm);
        postParam.put("date_from", GlobalVariable.rsm_report_date_from);
        postParam.put("date_to", GlobalVariable.rsm_report_date_to);
        postParam.put("area", GlobalVariable.rsm_report_area);
        postParam.put("collection", GlobalVariable.rsm_report_collection);
        postParam.put("style_no", GlobalVariable.rsm_report_style_no);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_RSM_WISE_PRODUCT_REPORT, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                productWiseSalesModelArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("data").getJSONObject(0).getJSONArray("secondary_sales");;

                                if (dataArray.length()>0){
                                    for (int i = 0; i<dataArray.length();i++){
                                        JSONObject dataObj = dataArray.getJSONObject(i);
                                        ProductWiseSalesModel dataModel = new ProductWiseSalesModel();
                                        dataModel.setStyle_no(dataObj.getString("style_no"));
                                        dataModel.setProduct(dataObj.getString("product"));
                                        dataModel.setQuantity(dataObj.getString("quantity"));
//                                        JSONArray detailsArray = dataObj.getJSONArray("order_details");
//                                        ArrayList<ProductWiseSalesDetailsModel> productWiseSalesDetailsModelArrayList = new ArrayList<ProductWiseSalesDetailsModel>();
//                                        for (int j=0;j<detailsArray.length();j++){
//                                            JSONObject detailsObj = detailsArray.getJSONObject(j);
//                                            ProductWiseSalesDetailsModel productWiseSalesDetailsModel = new ProductWiseSalesDetailsModel();
//                                            productWiseSalesDetailsModel.setColor(detailsObj.getString("color"));
//                                            productWiseSalesDetailsModel.setSize(detailsObj.getString("size"));
//                                            productWiseSalesDetailsModel.setQty(detailsObj.getString("qty"));
//                                            productWiseSalesDetailsModelArrayList.add(productWiseSalesDetailsModel);
//                                        }
//                                        dataModel.setProductWiseSalesDetailsModelArrayList(productWiseSalesDetailsModelArrayList);
                                        productWiseSalesModelArrayList.add(dataModel);
                                    }
                                }
                                productWiseSalesAdapter.notifyDataSetChanged();

                            }else {
                                dialogView.showSingleButtonDialog(RsmProductWiseReportResultActivity.this, getResources().getString(R.string.app_name),
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