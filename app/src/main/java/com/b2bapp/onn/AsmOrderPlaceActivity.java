package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class AsmOrderPlaceActivity extends AppCompatActivity {

    Spinner aseSp, storeSp;
    Button btnGotoProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asm_order_place);

        aseSp = findViewById(R.id.aseSp);
        storeSp = findViewById(R.id.storeSp);
        btnGotoProductList = findViewById(R.id.btnGotoProductList);


        btnGotoProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}