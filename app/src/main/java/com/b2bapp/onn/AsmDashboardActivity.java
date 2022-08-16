package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.LoginActivity;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AsmDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout relMyProfile, relSalesPersonWise, relDistributorWise,
            relLogout,relProductCatalogue, relScheme, relNotification,
            relAseWisePlaceOrder, relMyOrders;

    private TextView tvName, tvCount, tvDesignation;

    LinearLayout countLL;

    Preferences pref;
    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="AsmDashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asm_dashboard);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        relMyProfile = (RelativeLayout) findViewById(R.id.relMyProfile);
        relLogout = (RelativeLayout) findViewById(R.id.relLogout);
        relProductCatalogue = (RelativeLayout) findViewById(R.id.relProductCatalogue);
        relScheme = (RelativeLayout) findViewById(R.id.relScheme);
        relSalesPersonWise = (RelativeLayout) findViewById(R.id.relSalesPersonWise);
        relDistributorWise = (RelativeLayout) findViewById(R.id.relDistributorWise);
        relNotification = (RelativeLayout) findViewById(R.id.relNotification);
        relAseWisePlaceOrder = (RelativeLayout) findViewById(R.id.relAseWisePlaceOrder);
        tvCount = (TextView) findViewById(R.id.tvCount);
        tvDesignation = (TextView) findViewById(R.id.tvDesignation);
        countLL = (LinearLayout) findViewById(R.id.countLL);
        relMyOrders = (RelativeLayout) findViewById(R.id.relMyOrders);

        relMyProfile.setOnClickListener(this);
        relLogout.setOnClickListener(this);
        relProductCatalogue.setOnClickListener(this);
        relScheme.setOnClickListener(this);
        relSalesPersonWise.setOnClickListener(this);
        relDistributorWise.setOnClickListener(this);
        relNotification.setOnClickListener(this);
        relAseWisePlaceOrder.setOnClickListener(this);
        relMyOrders.setOnClickListener(this);

        //GlobalVariable.notiCount = 0;
        getNotificationList();

        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText("Welcome, " + pref.getStringPreference(AsmDashboardActivity.this, Preferences.User_fname) + " " +
                pref.getStringPreference(AsmDashboardActivity.this, Preferences.User_lname));

        if (pref.getStringPreference(AsmDashboardActivity.this, Preferences.User_User_type).equals("1")){

            tvDesignation.setText("Vice President");

        }else if(pref.getStringPreference(AsmDashboardActivity.this, Preferences.User_User_type).equals("2")){
            tvDesignation.setText("Regional Sales Manager");

        }else if(pref.getStringPreference(AsmDashboardActivity.this, Preferences.User_User_type).equals("3")){
            tvDesignation.setText("Area Sales Manager");

        }else if(pref.getStringPreference(AsmDashboardActivity.this, Preferences.User_User_type).equals("4")){
            tvDesignation.setText("Area Sales Executive");
        }else if(pref.getStringPreference(AsmDashboardActivity.this, Preferences.User_User_type).equals("5")){
            tvDesignation.setText("Distributor");
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.relMyProfile:
                startActivity(new Intent(AsmDashboardActivity.this, MyProfileActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relLogout:
                logoutAlertDialog(AsmDashboardActivity.this, "Logout Now!", "Do you want to exit from this application?");
                break;
            case R.id.relProductCatalogue:
                startActivity(new Intent(AsmDashboardActivity.this, AsmCatalogueActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relScheme:
                startActivity(new Intent(AsmDashboardActivity.this, AsmSchemeActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relSalesPersonWise:
                startActivity(new Intent(AsmDashboardActivity.this, AsmWiseTeamReportActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relDistributorWise:
                startActivity(new Intent(AsmDashboardActivity.this, AsmProductWiseReportActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relNotification:
                NotificationActivity.isfrom = "asm";
                startActivity(new Intent(AsmDashboardActivity.this, NotificationActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.relAseWisePlaceOrder:
                AsmWiseAseActivity.isFrom = "asm1";
                startActivity(new Intent(AsmDashboardActivity.this, AsmWiseAseActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.relMyOrders:
                AsmWiseAseActivity.isFrom = "asm";
                startActivity(new Intent(AsmDashboardActivity.this, AsmWiseAseActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
        }
    }

    /**
     * This method is for application logout
     * @param context
     * @param header
     * @param msg
     */
    public void logoutAlertDialog(final Activity context, String header, String msg){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.inflate_custom_alert_dialog, null);

        final android.app.AlertDialog alertD = new android.app.AlertDialog.Builder(context).create();
        TextView tvHeader=(TextView)promptView.findViewById(R.id.tvHeader);
        tvHeader.setText(header);
        TextView tvMsg=(TextView)promptView.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        Button btnCancel = (Button) promptView.findViewById(R.id.btnCancel);
        btnCancel.setText("Cancel");
        Button btnOk = (Button) promptView.findViewById(R.id.btnOk);
        btnOk.setText("Ok");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //pref.clearAllPref();
                pref.storeStringPreference(AsmDashboardActivity.this, Preferences.Id,"");
                pref.storeStringPreference(AsmDashboardActivity.this,Preferences.User_fname,"");
                pref.storeStringPreference(AsmDashboardActivity.this,Preferences.User_lname,"");
                pref.storeStringPreference(AsmDashboardActivity.this,Preferences.User_Email,"");
                pref.storeStringPreference(AsmDashboardActivity.this,Preferences.User_Mobile,"");
                pref.storeStringPreference(AsmDashboardActivity.this,Preferences.User_Employee_id,"");
                GlobalVariable.notiCount = 0;

                startActivity(new Intent(AsmDashboardActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                alertD.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertD.dismiss();

            }
        });

        alertD.setView(promptView);

        alertD.show();
    }

    private void getNotificationList() {
        dialogView.showCustomSpinProgress(AsmDashboardActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", pref.getStringPreference(AsmDashboardActivity.this, Preferences.Id));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_NOTIFICATION_LIST, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {

                                JSONArray jsonArray = response.getJSONArray("data");

                                int unread_count = 0;
                                for (int i = 0; i < jsonArray.length(); i++){
                                    if (jsonArray.getJSONObject(i).getString("read_flag").equals("0")){
                                        unread_count = unread_count + 1;
                                        Log.d("notification>>",String.valueOf(i));
                                    }
                                }

                                GlobalVariable.notiCount = unread_count;

                                if (GlobalVariable.notiCount > 0){
                                    countLL.setVisibility(View.VISIBLE);
                                    tvCount.setText(String.valueOf(GlobalVariable.notiCount));
                                }else{
                                    countLL.setVisibility(View.GONE);
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

}