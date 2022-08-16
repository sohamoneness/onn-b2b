package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.b2bapp.onn.base.LoginActivity;
import com.b2bapp.onn.utils.Preferences;

public class DistributorDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    Preferences pref;
    private RelativeLayout relPlaceOrder, relRetailerList, relOrderHistory, relProductCatalogue, relScheme, relLogout;
    private TextView tvName, tvDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_dashboard);

        getSupportActionBar().hide();

        pref=new Preferences(this);

        relPlaceOrder = (RelativeLayout) findViewById(R.id.relPlaceOrder);
        relRetailerList = (RelativeLayout) findViewById(R.id.relRetailerList);
        relOrderHistory = (RelativeLayout) findViewById(R.id.relOrderHistory);
        relProductCatalogue = (RelativeLayout) findViewById(R.id.relProductCatalogue);
        relScheme = (RelativeLayout) findViewById(R.id.relScheme);
        relLogout = (RelativeLayout) findViewById(R.id.relLogout);

        relPlaceOrder.setOnClickListener(this);
        relRetailerList.setOnClickListener(this);
        relOrderHistory.setOnClickListener(this);
        relProductCatalogue.setOnClickListener(this);
        relScheme.setOnClickListener(this);
        relLogout.setOnClickListener(this);

        tvDesignation = (TextView) findViewById(R.id.tvDesignation);
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText("Welcome, " + pref.getStringPreference(DistributorDashboardActivity.this, Preferences.User_fname) + " " +
                pref.getStringPreference(DistributorDashboardActivity.this, Preferences.User_lname));

        if (pref.getStringPreference(DistributorDashboardActivity.this, Preferences.User_User_type).equals("1")){

            tvDesignation.setText("Vice President");

        }else if(pref.getStringPreference(DistributorDashboardActivity.this, Preferences.User_User_type).equals("2")){
            tvDesignation.setText("Regional Sales Manager");

        }else if(pref.getStringPreference(DistributorDashboardActivity.this, Preferences.User_User_type).equals("3")){
            tvDesignation.setText("Area Sales Manager");

        }else if(pref.getStringPreference(DistributorDashboardActivity.this, Preferences.User_User_type).equals("4")){
            tvDesignation.setText("Area Sales Executive");
        }else if(pref.getStringPreference(DistributorDashboardActivity.this, Preferences.User_User_type).equals("5")){
            tvDesignation.setText("Distributor");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.relPlaceOrder:
                startActivity(new Intent(DistributorDashboardActivity.this, DistributorProductsActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relRetailerList:
                startActivity(new Intent(DistributorDashboardActivity.this, DistributorStoreListActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relOrderHistory:
                startActivity(new Intent(DistributorDashboardActivity.this, DistributorOrderListActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relProductCatalogue:
                startActivity(new Intent(DistributorDashboardActivity.this, DistributorCatalogueActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relScheme:
                startActivity(new Intent(DistributorDashboardActivity.this, DistributorSchemeActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.relLogout:
                logoutAlertDialog(DistributorDashboardActivity.this, "Logout Now!", "Do you want to exit from this application?");
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
                pref.storeStringPreference(DistributorDashboardActivity.this, Preferences.Id,"");
                pref.storeStringPreference(DistributorDashboardActivity.this,Preferences.User_fname,"");
                pref.storeStringPreference(DistributorDashboardActivity.this,Preferences.User_lname,"");
                pref.storeStringPreference(DistributorDashboardActivity.this,Preferences.User_Email,"");
                pref.storeStringPreference(DistributorDashboardActivity.this,Preferences.User_Mobile,"");
                pref.storeStringPreference(DistributorDashboardActivity.this,Preferences.User_Employee_id,"");

                startActivity(new Intent(DistributorDashboardActivity.this, LoginActivity.class)
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
}