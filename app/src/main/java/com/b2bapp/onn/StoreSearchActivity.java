package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.b2bapp.onn.adapter.StorelistAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.model.StoreModel;

import java.util.ArrayList;
import java.util.Locale;

public class StoreSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout liStore;
    private ImageView imgBack;
    private EditText etKeyword;
    private ListView storeList;
    ArrayList<StoreModel> storeModelArrayList = new ArrayList<StoreModel>();
    StorelistAdapter storelistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_search);

        getSupportActionBar().hide();

        imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(this);

        etKeyword = (EditText) findViewById(R.id.etKeyword);

        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Calling searchStore method
                searchStore(etKeyword.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //storeModelArrayList = GlobalVariable.storeModelArrayList;

        storeList = (ListView) findViewById(R.id.storeList);
        storelistAdapter = new StorelistAdapter(StoreSearchActivity.this,storeModelArrayList);
        storeList.setAdapter(storelistAdapter);

        storeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StoreDetailsActivity.storeModel = storeModelArrayList.get(i);
                GlobalVariable.storeModel = storeModelArrayList.get(i);
                startActivity(new Intent(StoreSearchActivity.this, StoreDetailsActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    /**
     * This method is for searching store
     * @param keyword
     */
    private void searchStore(String keyword){
        storeModelArrayList.clear();
        for (int i=0;i< GlobalVariable.storeModelArrayList.size();i++){
            String storeName = GlobalVariable.storeModelArrayList.get(i).getStore_name();

            if (storeName.toLowerCase().indexOf(keyword.toLowerCase())!=-1){
                storeModelArrayList.add(GlobalVariable.storeModelArrayList.get(i));
            }
        }

        storelistAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(StoreSearchActivity.this, StoreListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}