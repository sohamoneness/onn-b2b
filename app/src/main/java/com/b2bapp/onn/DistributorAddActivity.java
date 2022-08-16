package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DistributorAddActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private EditText etStoreName, etBusinessName, etContact, etWhatsApp, etEmail,
            etAddress, etArea, etState, etCity, etPin;
    private Button btnStoreAdd;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="DistributorAddActivity";
    Preferences pref;

    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_add);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(DistributorAddActivity.this,Preferences.Id);

        etStoreName = (EditText) findViewById(R.id.etStoreName);
        etBusinessName = (EditText) findViewById(R.id.etBusinessName);
        etContact = (EditText) findViewById(R.id.etContact);
        etWhatsApp = (EditText) findViewById(R.id.etWhatsApp);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etArea = (EditText) findViewById(R.id.etArea);
        etState = (EditText) findViewById(R.id.etState);
        etCity = (EditText) findViewById(R.id.etCity);
        etPin = (EditText) findViewById(R.id.etPin);

        btnStoreAdd = (Button) findViewById(R.id.btnStoreAdd);

        btnStoreAdd.setOnClickListener(this);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.btnStoreAdd:
                //Validation checking
                if (blankValidate()){
                    //Calling distributorAddTask method
                    distributorAddTask();
                }
                break;
        }
    }

    /**
     * This method is for adding a distributor
     */
    public void distributorAddTask(){
        dialogView.showCustomSpinProgress(DistributorAddActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("store_name", etStoreName.getText().toString());
        postParam.put("bussiness_name", etBusinessName.getText().toString());
        postParam.put("store_OCC_number", "");
        postParam.put("contact", etContact.getText().toString());
        postParam.put("whatsapp", etWhatsApp.getText().toString());
        postParam.put("email", etEmail.getText().toString());
        postParam.put("address", etAddress.getText().toString());
        postParam.put("area", etArea.getText().toString());
        postParam.put("state", etState.getText().toString());
        postParam.put("city", etCity.getText().toString());
        postParam.put("pin", etPin.getText().toString());
        Log.d("params>>",postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_DISTRIBUTOR_ADD, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                dialogView.showSingleButtonDialog(DistributorAddActivity.this, getResources().getString(R.string.app_name),
                                        response.getString("resp"));
                                GlobalVariable.distributorModelArrayList.clear();
                                startActivity(new Intent(DistributorAddActivity.this, DistributorActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(DistributorAddActivity.this, getResources().getString(R.string.app_name),
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
        Log.d("json req>>",jsonObjReq.toString());
        // Adding request to request queue
        mQueue.add(jsonObjReq);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DistributorAddActivity.this, DistributorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for blank validation
     * @return boolean
     */
    public boolean blankValidate(){
        if(etStoreName.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter store name");
            return false;
        }else if(etBusinessName.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter business name");
            return false;
        }else if(etContact.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter contact no");
            return false;
        }else if(etWhatsApp.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter whatsapp no");
            return false;
        }else if(etAddress.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter address");
            return false;
        }else if(etArea.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter area");
            return false;
        }else if(etState.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter state name");
            return false;
        }else if(etCity.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter city");
            return false;
        }else if(etPin.getText().toString().trim().equals("")){
            CToast.show(DistributorAddActivity.this,"Please enter pin code");
            return false;
        }
        return true;
    }
}