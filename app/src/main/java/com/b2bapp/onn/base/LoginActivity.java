package com.b2bapp.onn.base;

import androidx.appcompat.app.AppCompatActivity;

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
import com.b2bapp.onn.AsmDashboardActivity;
import com.b2bapp.onn.DistributorDashboardActivity;
import com.b2bapp.onn.EnterMobileActivity;
import com.b2bapp.onn.EnterOtpActivity;
import com.b2bapp.onn.R;
import com.b2bapp.onn.RsmDashboardActivity;
import com.b2bapp.onn.StartingActivity;
import com.b2bapp.onn.VpStartingActivity;
import com.b2bapp.onn.model.UserModel;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etMobile, etPassword;
    private Button btnLogin;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="LoginActivity";
    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        etMobile = (EditText) findViewById(R.id.etMobile);
        etPassword = (EditText) findViewById(R.id.etPassword);

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
                    loginTask(etMobile.getText().toString(),etPassword.getText().toString());
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
            CToast.show(LoginActivity.this,"Please enter 10 digit mobile no");
            return false;
        }else if(etPassword.getText().toString().trim().equals("")){
            CToast.show(LoginActivity.this,"Please enter password");
            return false;
        }
        return true;
    }

    public void loginTask(String mobileNo, String password){
        dialogView.showCustomSpinProgress(LoginActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("mobile", mobileNo);
        postParam.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_USER_LOGIN, new JSONObject(postParam),
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
                                userModel.setCity(userObj.getString("city"));

                                GlobalVariable.userModel = userModel;

                                pref.storeStringPreference(LoginActivity.this,Preferences.Id,userObj.getString("id"));
                                pref.storeStringPreference(LoginActivity.this,Preferences.User_fname,userObj.getString("fname"));
                                pref.storeStringPreference(LoginActivity.this,Preferences.User_lname,userObj.getString("lname"));
                                pref.storeStringPreference(LoginActivity.this,Preferences.User_Email,userObj.getString("email"));
                                pref.storeStringPreference(LoginActivity.this,Preferences.User_Mobile,userObj.getString("mobile"));
                                pref.storeStringPreference(LoginActivity.this,Preferences.User_Employee_id,userObj.getString("employee_id"));
                                pref.storeStringPreference(LoginActivity.this,Preferences.User_User_type,userObj.getString("user_type"));
                                pref.storeStringPreference(LoginActivity.this,Preferences.User_State,userObj.getString("state"));
                                pref.storeStringPreference(LoginActivity.this,Preferences.User_City,userObj.getString("city"));

                                if (userObj.getString("user_type").equals("1")){
                                    startActivity(new Intent(LoginActivity.this, VpStartingActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("2")){
                                    startActivity(new Intent(LoginActivity.this, RsmDashboardActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("3")){
                                    startActivity(new Intent(LoginActivity.this, AsmDashboardActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("5")){
                                    startActivity(new Intent(LoginActivity.this, DistributorDashboardActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("4")){
                                    startActivity(new Intent(LoginActivity.this, StartingActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }

                            }else {
                                dialogView.showSingleButtonDialog(LoginActivity.this, getResources().getString(R.string.app_name),
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
    public void onBackPressed() {
        Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
        homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
        homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreenIntent);
    }
}