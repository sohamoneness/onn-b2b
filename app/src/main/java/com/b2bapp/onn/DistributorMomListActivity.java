package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.DistributorListAdapter;
import com.b2bapp.onn.adapter.DistributorMomListAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.DistributorModel;
import com.b2bapp.onn.model.DistributorMomModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DistributorMomListActivity extends AppCompatActivity {

    ImageView ivBack;
    RecyclerView momRv;
    String distributorName;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="DistributorMomListActivity";
    Preferences pref;

    String range;
    final Calendar myCalendar= Calendar.getInstance();
    final Calendar myCalendar1= Calendar.getInstance();

    EditText etStart, etEnd;
    TextView tvSearch;

    String fromDate="", toDate="";

    List<DistributorMomModel> distMomList = new ArrayList<>();

    DistributorMomListAdapter distributorMomListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_mom_list);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        ivBack = findViewById(R.id.imgBack);
        momRv = findViewById(R.id.momRv);
        etStart = findViewById(R.id.etStartDate);
        etEnd = findViewById(R.id.etEndDate);
        tvSearch = findViewById(R.id.tvSearch);

        distributorName = getIntent().getStringExtra("Dist_Name");

        getMomList(distributorName);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        etStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                range = "from";
                DatePickerDialog datePickerDialog = new DatePickerDialog(DistributorMomListActivity.this, date1, myCalendar
                        .get(android.icu.util.Calendar.YEAR), myCalendar.get(android.icu.util.Calendar.MONTH),
                        myCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        etEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                range = "to";
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(DistributorMomListActivity.this, date2, myCalendar1
                        .get(android.icu.util.Calendar.YEAR), myCalendar1.get(android.icu.util.Calendar.MONTH),
                        myCalendar1.get(android.icu.util.Calendar.DAY_OF_MONTH));
                datePickerDialog1.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog1.show();
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromDate.equals("") && toDate.equals("")){
                    dialogView.showSingleButtonDialog(DistributorMomListActivity.this, getResources().getString(R.string.app_name), "Please select Start and End date to filter.");
                }else if (fromDate.equals("") || toDate.equals("")){
                    dialogView.showSingleButtonDialog(DistributorMomListActivity.this, getResources().getString(R.string.app_name), "Please select Start and End date to filter.");
                }else{
                    getMomList(distributorName);
                }


            }
        });



    }

    final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(android.icu.util.Calendar.YEAR, year);
            myCalendar.set(android.icu.util.Calendar.MONTH, monthOfYear);
            myCalendar.set(android.icu.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            /*if(datetype.equalsIgnoreCase("From"))
            {
                updateLabel(etFrom);
            }
            else if(datetype.equalsIgnoreCase("To"))
            {*/
            //if (range.equals("from")){
            updateLabel(etStart);
            //}else if (range.equals("to")){
            //    updateLabel(tvTo);
            //}

            // }


        }

    };
    final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar1.set(android.icu.util.Calendar.YEAR, year);
            myCalendar1.set(android.icu.util.Calendar.MONTH, monthOfYear);
            myCalendar1.set(android.icu.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            /*if(datetype.equalsIgnoreCase("From"))
            {
                updateLabel(etFrom);
            }
            else if(datetype.equalsIgnoreCase("To"))
            {*/
            /*if (range.equals("from")){
                updateLabel(tvFrom);
            }else if (range.equals("to")){*/
            updateLabel(etEnd);
            //}

            // }


        }

    };

    private void updateLabel(EditText editText) {
        //   String myFormat = "yyyy-MM-dd"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());


        if (range.equals("from")) {
            fromDate = sdf.format(myCalendar.getTime());
            editText.setText(sdf.format(myCalendar.getTime()));
        } else if (range.equals("to")) {
            editText.setText(sdf.format(myCalendar1.getTime()));
            toDate = sdf.format(myCalendar1.getTime());
        }

    }

    private void getMomList(String distributorName) {
        dialogView.showCustomSpinProgress(DistributorMomListActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", pref.getStringPreference(this, Preferences.Id));
        postParam.put("distributor_name", distributorName);
        postParam.put("date_from", fromDate);
        postParam.put("date_to", toDate);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_DISTRIBUTOR_MOM_LIST, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                distMomList.clear();
                                JSONArray distributorsMomArray = response.getJSONArray("data");

                                if (distributorsMomArray.length() > 0) {
                                    for (int i = 0; i < distributorsMomArray.length(); i++) {

                                        DistributorMomModel dmm = new DistributorMomModel();
                                        dmm.setId(distributorsMomArray.getJSONObject(i).getString("id"));
                                        dmm.setUser_id(distributorsMomArray.getJSONObject(i).getString("user_id"));
                                        dmm.setDistributor_name(distributorsMomArray.getJSONObject(i).getString("distributor_name"));
                                        dmm.setComment(distributorsMomArray.getJSONObject(i).getString("comment"));
                                        dmm.setCreated_at(distributorsMomArray.getJSONObject(i).getString("created_at"));
                                        dmm.setUpdated_at(distributorsMomArray.getJSONObject(i).getString("created_at"));

                                        distMomList.add(dmm);

                                    }
                                }

                                if (distMomList.size() > 0){
                                    distributorMomListAdapter = new DistributorMomListAdapter(DistributorMomListActivity.this, distMomList);
                                    momRv.setLayoutManager(new LinearLayoutManager(
                                            DistributorMomListActivity.this,
                                            LinearLayoutManager.VERTICAL,
                                            false));
                                    momRv.setAdapter(distributorMomListAdapter);
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