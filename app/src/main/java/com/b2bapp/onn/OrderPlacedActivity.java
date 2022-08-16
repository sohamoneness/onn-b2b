package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;

import com.b2bapp.onn.utils.Preferences;

public class OrderPlacedActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnGotoDashboard;
    Preferences preference;
    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        getSupportActionBar().hide();

        preference = new Preferences(this);
        userType = preference.getStringPreference(OrderPlacedActivity.this,Preferences.User_User_type);

        btnGotoDashboard = (Button) findViewById(R.id.btnGotoDashboard);
        btnGotoDashboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGotoDashboard:
                if (userType.equals("3")){
                    startActivity(new Intent(OrderPlacedActivity.this, AsmDashboardActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }else{
                    startActivity(new Intent(OrderPlacedActivity.this, TaskActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrderPlacedActivity.this, TaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}