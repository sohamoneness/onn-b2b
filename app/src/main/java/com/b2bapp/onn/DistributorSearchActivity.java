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
import android.widget.ListView;

import com.b2bapp.onn.adapter.DistributorListAdapter;
import com.b2bapp.onn.adapter.StorelistAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.model.DistributorModel;
import com.b2bapp.onn.model.StoreModel;

import java.util.ArrayList;

public class DistributorSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private EditText etKeyword;
    private ListView distributorList;
    ArrayList<DistributorModel> distributorModelArrayList = new ArrayList<DistributorModel>();
    DistributorListAdapter distributorListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_search);

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

        distributorList = (ListView) findViewById(R.id.distributorList);
        distributorListAdapter = new DistributorListAdapter(DistributorSearchActivity.this,distributorModelArrayList);
        distributorList.setAdapter(distributorListAdapter);

        distributorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    /**
     * This method is for searching store
     * @param keyword
     */
    private void searchStore(String keyword){
        distributorModelArrayList.clear();
        for (int i = 0; i< GlobalVariable.distributorModelArrayList.size(); i++){
            String storeName = GlobalVariable.distributorModelArrayList.get(i).getStore_name();

            if (storeName.toLowerCase().indexOf(keyword.toLowerCase())!=-1){
                distributorModelArrayList.add(GlobalVariable.distributorModelArrayList.get(i));
            }
        }

        distributorListAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(DistributorSearchActivity.this, DistributorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}