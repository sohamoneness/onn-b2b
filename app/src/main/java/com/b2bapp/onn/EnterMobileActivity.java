package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnterMobileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etMobile;
    private Button btnLogin;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="EnterMobileActivity";
    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        etMobile = (EditText) findViewById(R.id.etMobile);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                //Validation checking
                if (blankValidate()){
                    //Calling getOtpTask method
                    getOtpTask(etMobile.getText().toString());
                }
                break;
        }
    }

    /**
     * This method is for blank validation
     * @return boolean
     */
    public boolean blankValidate(){
        if(etMobile.getText().toString().trim().equals("")){
            CToast.show(EnterMobileActivity.this,"Please enter 10 digit mobile no");
            return false;
        }
        return true;
    }

    /**
     * This method is for generating OTP
     * @param mobileNo
     */
    public void getOtpTask(String mobileNo){
        dialogView.showCustomSpinProgress(EnterMobileActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("mobile", mobileNo);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_GET_OTP, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                EnterOtpActivity.mobileNo = mobileNo;
                                startActivity(new Intent(EnterMobileActivity.this, EnterOtpActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(EnterMobileActivity.this, getResources().getString(R.string.app_name),
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