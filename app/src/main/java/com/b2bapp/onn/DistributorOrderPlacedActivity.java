package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DistributorOrderPlacedActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnGotoDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_order_placed);

        getSupportActionBar().hide();

        btnGotoDashboard = (Button) findViewById(R.id.btnGotoDashboard);
        btnGotoDashboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGotoDashboard:
                startActivity(new Intent(DistributorOrderPlacedActivity.this, DistributorDashboardActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DistributorOrderPlacedActivity.this, DistributorDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}