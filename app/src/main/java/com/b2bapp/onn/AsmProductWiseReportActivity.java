package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.SpinCollectionAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.CollectionModel;
import com.b2bapp.onn.model.SearchCollectionModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AsmProductWiseReportActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private EditText etStartDate, etEndDate;//, etProductStyle;
    private Spinner spinCollection, productStyleSp;
    private Button btnSubmit;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="AseProductWiseReportActivity";
    Preferences pref;

    final Calendar myCalendar= Calendar.getInstance();

    ArrayList<CollectionModel> collectionModelArrayList = new ArrayList<CollectionModel>();
    ArrayList<SearchCollectionModel> searchCollectionModelArrayList = new ArrayList<SearchCollectionModel>();

    private SpinCollectionAdapter spinCollectionAdapter;

    private String start = "";
    private String end = "";
    private String selected_collection = "";
    List<String> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asm_product_wise_report);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);

        end = dateFormat.format(myCalendar.getTime());

        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);

        start = dateFormat.format(c.getTime());

        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
        //etProductStyle = (EditText) findViewById(R.id.etProductStyle);

        spinCollection = (Spinner) findViewById(R.id.spinCollection);
        productStyleSp = (Spinner) findViewById(R.id.productStyleSp);

        spinCollectionAdapter = new SpinCollectionAdapter(collectionModelArrayList,AsmProductWiseReportActivity.this);
        spinCollection.setAdapter(spinCollectionAdapter);

        spinCollection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_collection = collectionModelArrayList.get(i).getId();
                if (selected_collection.equals("")){
                    selected_collection = "10000";
                    getCollectionWiseProductStyles(selected_collection);
                }else{
                    getCollectionWiseProductStyles(selected_collection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imgBack = (ImageView) findViewById(R.id.imgBack);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        getCollectionList();

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel1();
            }
        };
        DatePickerDialog.OnDateSetListener date1 =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel2();
            }
        };
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AsmProductWiseReportActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AsmProductWiseReportActivity.this,date1,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if (GlobalVariable.product_date_from.equals("")){
            etStartDate.setText(start);
        }else{
            etStartDate.setText(GlobalVariable.product_date_from);
        }

        if (GlobalVariable.product_date_to.equals("")){
            etEndDate.setText(end);
        }else{
            etEndDate.setText(GlobalVariable.product_date_to);
        }

       // etProductStyle.setText(GlobalVariable.product_style_no);
    }

    private void getCollectionWiseProductStyles(String selected_collection) {
        dialogView.showCustomSpinProgress(AsmProductWiseReportActivity.this);
        Map<String, String> postParam = new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_COLLECTION_WISE_PRODUCT_STYLES + "/" + selected_collection, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG + "->styles>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {

                            if (!response.getBoolean("error")) {
                                searchCollectionModelArrayList.clear();
                                products.clear();
                                //String[] products = {};

                                JSONArray categoriesArray = response.getJSONObject("data").getJSONArray("product");

                                if (categoriesArray.length() > 0) {
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        SearchCollectionModel collectionModel = new SearchCollectionModel();

                                        collectionModel.setProduct_id(categoryObj.getString("product_id"));
                                        collectionModel.setProduct_name(categoryObj.getString("product_name"));
                                        collectionModel.setProduct_style_no(categoryObj.getString("product_style_no"));

                                        searchCollectionModelArrayList.add(collectionModel);

                                    }
                                }

                                if (searchCollectionModelArrayList.size() > 0) {
                                    for (int j = 0; j < searchCollectionModelArrayList.size(); j++) {
                                        products.add(searchCollectionModelArrayList.get(j).getProduct_style_no());
                                    }

                                    ArrayAdapter<String> spinnerPhoneArrayAdapter2 = new ArrayAdapter<String>(AsmProductWiseReportActivity.this, R.layout.spinner_item, products);
                                    //spinnerPhoneArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
                                    productStyleSp.setAdapter(spinnerPhoneArrayAdapter2);
                                    productStyleSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                            String styleNo = products.get(i);

                                            GlobalVariable.store_report_style_no = styleNo;
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {


                                        }
                                    });
                                }else {
                                   /* products.clear();
                                    ArrayAdapter<String> spinnerPhoneArrayAdapter2 = new ArrayAdapter<String>(AseProductWiseReportActivity.this, R.layout.spinner_item, products);
                                    //spinnerPhoneArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
                                    productStyleSp.setAdapter(spinnerPhoneArrayAdapter2);*/
                                }

                            }

                            // adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                dialogView.dismissCustomSpinProgress();
                /*products.clear();
                ArrayAdapter<String> spinnerPhoneArrayAdapter2 = new ArrayAdapter<String>(AseProductWiseReportActivity.this, R.layout.spinner_item, products);
                //spinnerPhoneArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
                productStyleSp.setAdapter(spinnerPhoneArrayAdapter2);*/
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

    private void updateLabel1(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etStartDate.setText(dateFormat.format(myCalendar.getTime()));
        start = dateFormat.format(myCalendar.getTime());
    }

    private void updateLabel2(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etEndDate.setText(dateFormat.format(myCalendar.getTime()));
        end = dateFormat.format(myCalendar.getTime());
    }

    public void getCollectionList(){
        dialogView.showCustomSpinProgress(AsmProductWiseReportActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_COLLECTION_LIST, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->collectins>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {

                            if (!response.getBoolean("error")) {
                                collectionModelArrayList.clear();
                                JSONArray categoriesArray = response.getJSONArray("data");

                                if (categoriesArray.length() > 0) {
                                    CollectionModel collectionModel1 = new CollectionModel();
                                    collectionModel1.setId("");
                                    collectionModel1.setName("All");
                                    collectionModel1.setPosition("");
                                    collectionModelArrayList.add(collectionModel1);

                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        CollectionModel collectionModel = new CollectionModel();

                                        collectionModel.setId(categoryObj.getString("id"));
                                        collectionModel.setName(categoryObj.getString("name"));
                                        collectionModel.setPosition(categoryObj.getString("position"));

                                        collectionModelArrayList.add(collectionModel);
                                    }
                                }

                                spinCollectionAdapter.notifyDataSetChanged();

                                for (int i=0;i<collectionModelArrayList.size();i++){
                                    if (collectionModelArrayList.get(i).getId().equals(GlobalVariable.product_collection)){
                                        spinCollection.setSelection(i);
                                    }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.btnSubmit:
                submitReportData();
                break;
        }
    }

    public void submitReportData(){
        GlobalVariable.product_date_from = etStartDate.getText().toString();
        GlobalVariable.product_date_to = etEndDate.getText().toString();
        GlobalVariable.product_collection = selected_collection;
        //GlobalVariable.product_style_no = etProductStyle.getText().toString();

        startActivity(new Intent(AsmProductWiseReportActivity.this, AsmProductWiseReportResultActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AsmProductWiseReportActivity.this, AsmDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}