package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.UserModel;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnterOtpActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private Button btnVerify;
    private EditText etNum1, etNum2, etNum3, etNum4;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="EnterOtpActivity";
    Preferences pref;

    public static String mobileNo = "";
    private String otp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        etNum1 = (EditText) findViewById(R.id.etNum1);
        etNum2 = (EditText) findViewById(R.id.etNum2);
        etNum3 = (EditText) findViewById(R.id.etNum3);
        etNum4 = (EditText) findViewById(R.id.etNum4);

        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(this);

//        etNum1.addTextChangedListener(this);
//        etNum2.addTextChangedListener(this);
//        etNum3.addTextChangedListener(this);
//        etNum4.addTextChangedListener(this);
        etNum1.addTextChangedListener(new GenericTextWatcher(etNum1));
        etNum2.addTextChangedListener(new GenericTextWatcher(etNum2));
        etNum3.addTextChangedListener(new GenericTextWatcher(etNum3));
        etNum4.addTextChangedListener(new GenericTextWatcher(etNum4));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnVerify:
                otp = etNum1.getText().toString()+etNum2.getText().toString()+etNum3.getText().toString()+etNum4.getText().toString();

                if (otp.equals("") || otp.length()<4){
                    //Error show in case of invalid otp
                    CToast.show(EnterOtpActivity.this,"Please enter a valid OTP");
                }else{
                    //Calling loginWithOtpTask method
                    loginWithOtpTask(mobileNo,otp);
                }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(etNum1.getText().toString().length()==1){
            etNum2.requestFocus();
        }

        if(etNum2.getText().toString().length()==1){
            etNum3.requestFocus();
        }

        if(etNum3.getText().toString().length()==1){
            etNum4.requestFocus();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * This method is for login with OTP
     * @param mobileNo
     * @param otp
     */
    public void loginWithOtpTask(String mobileNo, String otp){
        dialogView.showCustomSpinProgress(EnterOtpActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("mobile", mobileNo);
        postParam.put("otp", otp);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_LOGIN_WITH_OTP, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONObject userObj = response.getJSONObject("data");

                                UserModel userModel = new UserModel();

                                userModel.setId(userObj.getString("id"));
                                userModel.setFname(userObj.getString("fname"));
                                userModel.setLname(userObj.getString("lname"));
                                userModel.setEmail(userObj.getString("email"));
                                userModel.setMobile(userObj.getString("mobile"));
                                userModel.setWhatsapp_no(userObj.getString("whatsapp_no"));
                                userModel.setEmployee_id(userObj.getString("employee_id"));
                                userModel.setUser_type(userObj.getString("user_type"));
                                userModel.setState(userObj.getString("state"));

                                GlobalVariable.userModel = userModel;

                                pref.storeStringPreference(EnterOtpActivity.this,Preferences.Id,userObj.getString("id"));
                                pref.storeStringPreference(EnterOtpActivity.this,Preferences.User_fname,userObj.getString("fname"));
                                pref.storeStringPreference(EnterOtpActivity.this,Preferences.User_lname,userObj.getString("lname"));
                                pref.storeStringPreference(EnterOtpActivity.this,Preferences.User_Email,userObj.getString("email"));
                                pref.storeStringPreference(EnterOtpActivity.this,Preferences.User_Mobile,userObj.getString("mobile"));
                                pref.storeStringPreference(EnterOtpActivity.this,Preferences.User_Employee_id,userObj.getString("employee_id"));
                                pref.storeStringPreference(EnterOtpActivity.this,Preferences.User_User_type,userObj.getString("user_type"));
                                pref.storeStringPreference(EnterOtpActivity.this,Preferences.User_State,userObj.getString("state"));

                                if (userObj.getString("user_type").equals("1")){
                                    startActivity(new Intent(EnterOtpActivity.this, VpStartingActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("2")){
                                    startActivity(new Intent(EnterOtpActivity.this, RsmDashboardActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("5")){
                                    startActivity(new Intent(EnterOtpActivity.this, DistributorDashboardActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("4")){
                                    startActivity(new Intent(EnterOtpActivity.this, StartingActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }

                            }else {
                                dialogView.showSingleButtonDialog(EnterOtpActivity.this, getResources().getString(R.string.app_name),
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

    public class GenericTextWatcher implements TextWatcher
    {
        private View view;
        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch(view.getId())
            {

                case R.id.etNum1:
                    if(text.length()==1)
                        etNum2.requestFocus();
                    break;
                case R.id.etNum2:
                    if(text.length()==1)
                        etNum3.requestFocus();
                    else if(text.length()==0)
                        etNum3.requestFocus();
                    break;
                case R.id.etNum3:
                    if(text.length()==1)
                        etNum4.requestFocus();
                    else if(text.length()==0)
                        etNum2.requestFocus();
                    break;
                case R.id.etNum4:
                    if(text.length()==0)
                        etNum3.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }
}