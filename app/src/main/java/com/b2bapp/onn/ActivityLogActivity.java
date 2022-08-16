package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.ActivityLogAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.ActivityLogModel;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.model.UserModel;
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
import java.util.Locale;
import java.util.Map;

public class ActivityLogActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imgBack;
    private TextView tvDay, tvDate;
    private ListView logList;
    private EditText etStartDate;
    ArrayList<ActivityLogModel> activityLogModelArrayList = new ArrayList<ActivityLogModel>();
    ActivityLogAdapter activityLogAdapter;

    final Calendar myCalendar= Calendar.getInstance();

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="ActivityLogActivity";
    Preferences pref;

    String userId = "";
    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        etStartDate = (EditText) findViewById(R.id.etStartDate);

        String myFormat="dd-MM-yyyy";
        String myFormat1="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat dateFormat1=new SimpleDateFormat(myFormat1, Locale.US);

        etStartDate.setText(dateFormat.format(myCalendar.getTime()));
        selectedDate = dateFormat1.format(myCalendar.getTime());

        userId = pref.getStringPreference(ActivityLogActivity.this,Preferences.Id);

        tvDay = (TextView) findViewById(R.id.tvDay);
        tvDate = (TextView) findViewById(R.id.tvDate);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String currentDateandTime = sdf.format(new Date());
        tvDay.setText(currentDateandTime);

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yyyy");
        String currentDateandTime1 = sdf1.format(new Date());
        tvDate.setText(currentDateandTime1);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        logList = (ListView) findViewById(R.id.logList);
        activityLogAdapter = new ActivityLogAdapter(ActivityLogActivity.this,activityLogModelArrayList);
        logList.setAdapter(activityLogAdapter);

        //Calling getActivityLogTask method
        getActivityLogTask(userId,selectedDate);

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel1();
            }
        };
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ActivityLogActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel1(){
        String myFormat="dd-MM-yyyy";
        String myFormat1="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat dateFormat1=new SimpleDateFormat(myFormat1, Locale.US);
        etStartDate.setText(dateFormat.format(myCalendar.getTime()));
        selectedDate = dateFormat1.format(myCalendar.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String currentDateandTime = sdf.format(myCalendar.getTime());
        tvDay.setText(currentDateandTime);

        //Calling getActivityLogTask method
        getActivityLogTask(userId,selectedDate);
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
        Intent intent = new Intent(ActivityLogActivity.this, TaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for getting user activity log
     * @param userId
     */
    public void getActivityLogTask(String userId,String selectedDate){
        dialogView.showCustomSpinProgress(ActivityLogActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("date", selectedDate);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_USER_ACTIVITY_LOG, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+">>activity log", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                activityLogModelArrayList.clear();

                                if (response.has("data")){


                                JSONArray logsArray = response.getJSONArray("data");

                                if (logsArray.length() > 0) {
                                    for (int i = 0; i < logsArray.length(); i++) {
                                        JSONObject logObj = logsArray.getJSONObject(i);
                                        ActivityLogModel activityLogModel = new ActivityLogModel();

                                        activityLogModel.setId(logObj.getString("id"));
                                        activityLogModel.setDate(logObj.getString("date"));
                                        activityLogModel.setTime(logObj.getString("time"));
                                        activityLogModel.setUser_id(logObj.getString("user_id"));
                                        activityLogModel.setType(logObj.getString("type"));
                                        activityLogModel.setComment(logObj.getString("comment"));
                                        activityLogModel.setLat(logObj.getString("lat"));
                                        activityLogModel.setLng(logObj.getString("lng"));
                                        activityLogModel.setLocation(logObj.getString("location"));

                                        if (!logObj.getString("type").equals("login")){
                                            activityLogModelArrayList.add(activityLogModel);
                                        }

                                    }
                                }

                                activityLogAdapter.notifyDataSetChanged();
                                }else{
                                    activityLogModelArrayList.clear();
                                    activityLogAdapter.notifyDataSetChanged();
                                }
                            }else {
                                dialogView.showSingleButtonDialog(ActivityLogActivity.this, getResources().getString(R.string.app_name),
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
}