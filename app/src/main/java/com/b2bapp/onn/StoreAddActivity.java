package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.AreaAdapter;
import com.b2bapp.onn.adapter.DistributorListAdapter;
import com.b2bapp.onn.adapter.DistributorSpinAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.VolleyMultipartRequest;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.AreaModel;
import com.b2bapp.onn.model.DistributorModel;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.Config;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StoreAddActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private EditText etStoreName, etBusinessName, etContact, etWhatsApp, etEmail, etOwnerName,
            etAddress, etArea, etState, etCity, etPin;
    private Button btnStoreAdd;
    private ImageView imgStore;
    TextView tvOwnerDob, tvOwnerAnni, tvContactDob, tvContactAnni;
    EditText etContactName, etContactPhn, etContactWhatsapp;
    CheckBox chkSame;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG = "StoreAddActivity";
    Preferences pref;

    String userId = "";

    Bitmap myBitmap;
    Uri picUri;

    String distributor_name = "";
    final Calendar myCalendar = Calendar.getInstance();

    String onClicked = "";


    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 107;
    ArrayList<DistributorModel> distributorModelArrayList = new ArrayList<DistributorModel>();
    DistributorSpinAdapter distributorListAdapter;

    private Spinner distributorSpin, areaSpin;
    String ase = "";
    TextView tvPath;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private RequestQueue rQueue;
    String imgURL = "";
    String state = "";
    String selected_area = "";

    AreaAdapter areaAdapter;
    ArrayList<AreaModel> areaModelArrayList = new ArrayList<AreaModel>();

    LinearLayout contactLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_add);

        getSupportActionBar().hide();

        pref = new Preferences(this);
        dialogView = new DialogView();
        mQueue = Volley.newRequestQueue(this);

        ase = pref.getStringPreference(StoreAddActivity.this, Preferences.User_fname)
                + " " + pref.getStringPreference(StoreAddActivity.this, Preferences.User_lname);

        userId = pref.getStringPreference(StoreAddActivity.this, Preferences.Id);
        state = pref.getStringPreference(StoreAddActivity.this, Preferences.User_State);

        etStoreName = (EditText) findViewById(R.id.etStoreName);
        etBusinessName = (EditText) findViewById(R.id.etBusinessName);
        etContact = (EditText) findViewById(R.id.etContact);
        etWhatsApp = (EditText) findViewById(R.id.etWhatsApp);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etArea = (EditText) findViewById(R.id.etArea);
        etState = (EditText) findViewById(R.id.etState);
        etCity = (EditText) findViewById(R.id.etCity);
        etPin = (EditText) findViewById(R.id.etPin);
        contactLL = (LinearLayout) findViewById(R.id.contactLL);

        etContactName = (EditText) findViewById(R.id.etContactName);
        etContactPhn = (EditText) findViewById(R.id.etContactPhn);
        etContactWhatsapp = (EditText) findViewById(R.id.etContactWhatsapp);
        etOwnerName = (EditText) findViewById(R.id.etOwnerName);
        tvOwnerDob = (TextView) findViewById(R.id.tvOwnerDob);
        tvOwnerAnni = (TextView) findViewById(R.id.tvOwnerAnni);
        tvContactDob = (TextView) findViewById(R.id.tvContactDob);
        tvContactAnni = (TextView) findViewById(R.id.tvContactAnni);

        tvOwnerDob.setOnClickListener(this);
        tvOwnerAnni.setOnClickListener(this);
        tvContactDob.setOnClickListener(this);
        tvContactAnni.setOnClickListener(this);

        chkSame = (CheckBox) findViewById(R.id.chkSame);

        btnStoreAdd = (Button) findViewById(R.id.btnStoreAdd);

        btnStoreAdd.setOnClickListener(this);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        imgStore = (ImageView) findViewById(R.id.imgStore);
        imgStore.setOnClickListener(this);

        distributorSpin = (Spinner) findViewById(R.id.distributorSpin);

        chkSame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();

                if (checked) {
                    etContactName.setText(etOwnerName.getText().toString());
                    etContactPhn.setText(etContact.getText().toString());
                    etContactWhatsapp.setText(etWhatsApp.getText().toString());
                    tvContactDob.setText(tvOwnerDob.getText().toString());
                    tvContactAnni.setText(tvOwnerAnni.getText().toString());
                   // contactLL.setVisibility(View.GONE);


                } else {
                    etContactName.setText("");
                    etContactPhn.setText("");
                    etContactWhatsapp.setText("");
                    tvContactDob.setText("");
                    tvContactAnni.setText("");
                    //contactLL.setVisibility(View.VISIBLE);
                }

            }
        });

        distributorSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                distributor_name = distributorModelArrayList.get(i).getBussiness_name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etState.setText(state);

        areaSpin = (Spinner) findViewById(R.id.areaSpin);
        areaAdapter = new AreaAdapter(areaModelArrayList, StoreAddActivity.this);
        areaSpin.setAdapter(areaAdapter);

        areaSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_area = areaModelArrayList.get(i).getName();
                Log.d("selected_area>>", selected_area);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 6)
                    getStateFromPin(etPin.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        getDistributorListTask();
    }

    private void getStateFromPin(String pin) {
        dialogView.showCustomSpinProgress(StoreAddActivity.this);

        Map<String, String> postParam = new HashMap<String, String>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                //WebServices.URL_STORE_VISIT_END+"/"+pref.getStringPreference(TaskActivity.this,Preferences.Visit_id), new JSONObject(postParam),
                WebServices.URL_GET_STATE_FROM_PIN + pin, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (response.length() > 0) {
                                JSONArray jsonArray = response;
                                if (jsonArray.getJSONObject(0).getString("Status").equals("Success")) {
                                    JSONArray jArray = jsonArray.getJSONObject(0).getJSONArray("PostOffice");
                                    String State = jArray.getJSONObject(0).getString("State");
                                    etState.setText(State);

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                if (error.equals("invalid_credentials")) {
                    Log.e("invalid>>", "You have entered a wrong credentials");
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

        jsonArrayRequest.setTag(TAG);
        // Adding request to request queue
        mQueue.add(jsonArrayRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.btnStoreAdd:
                //Validation checking
                if (blankValidate()) {
                    //Calling storeAddTask method
                    storeAddTask();
                }
                break;
            case R.id.imgStore:
                if (checkAndRequestPermissions(StoreAddActivity.this)) {
                    chooseImage(StoreAddActivity.this);
                }
                break;
            case R.id.tvOwnerDob:
                onClicked = "oDOB";
                DatePickerDialog datePickerDialog = new DatePickerDialog(StoreAddActivity.this, date1, myCalendar
                        .get(android.icu.util.Calendar.YEAR), myCalendar.get(android.icu.util.Calendar.MONTH),
                        myCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
                break;
            case R.id.tvOwnerAnni:
                onClicked = "oANNI";
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(StoreAddActivity.this, date1, myCalendar
                        .get(android.icu.util.Calendar.YEAR), myCalendar.get(android.icu.util.Calendar.MONTH),
                        myCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH));
                //datePickerDialog1.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog1.show();
                break;
            case R.id.tvContactDob:
                onClicked = "cDOB";
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(StoreAddActivity.this, date1, myCalendar
                        .get(android.icu.util.Calendar.YEAR), myCalendar.get(android.icu.util.Calendar.MONTH),
                        myCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH));
                //datePickerDialog2.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog2.show();
                break;
            case R.id.tvContactAnni:
                onClicked = "cANNI";
                DatePickerDialog datePickerDialog3 = new DatePickerDialog(StoreAddActivity.this, date1, myCalendar
                        .get(android.icu.util.Calendar.YEAR), myCalendar.get(android.icu.util.Calendar.MONTH),
                        myCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog3.show();
                break;
        }
    }


    final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(android.icu.util.Calendar.YEAR, year);
            myCalendar.set(android.icu.util.Calendar.MONTH, monthOfYear);
            myCalendar.set(android.icu.util.Calendar.DAY_OF_MONTH, dayOfMonth);


            if (onClicked.equals("oDOB")) {
                updateLabel(tvOwnerDob);

            } else if (onClicked.equals("oANNI")) {
                updateLabel(tvOwnerAnni);

            } else if (onClicked.equals("cDOB")) {
                updateLabel(tvContactDob);

            } else if (onClicked.equals("cANNI")) {
                updateLabel(tvContactAnni);
            }
        }

    };

    private void updateLabel(TextView editText) {
        //   String myFormat = "yyyy-MM-dd"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());


        editText.setText(sdf.format(myCalendar.getTime()));


    }

    /**
     * This method is for adding a store
     */
    public void storeAddTask() {
        dialogView.showCustomSpinProgress(StoreAddActivity.this);
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("store_name", etStoreName.getText().toString());
        postParam.put("bussiness_name", etBusinessName.getText().toString());
        postParam.put("store_OCC_number", "");
        postParam.put("contact", etContact.getText().toString());
        postParam.put("whatsapp", etWhatsApp.getText().toString());
        postParam.put("email", etEmail.getText().toString());
        postParam.put("address", etAddress.getText().toString());
        postParam.put("area", selected_area);
        postParam.put("state", etState.getText().toString());
        postParam.put("city", etCity.getText().toString());
        postParam.put("pin", etPin.getText().toString());
        postParam.put("owner_name", etOwnerName.getText().toString());
        postParam.put("date_of_birth", tvOwnerDob.getText().toString());
        postParam.put("date_of_anniversary", tvOwnerAnni.getText().toString());
        postParam.put("contact_person", etContact.getText().toString());
        postParam.put("contact_person_phone", etContactPhn.getText().toString());
        postParam.put("contact_person_whatsapp", etContactWhatsapp.getText().toString());
        postParam.put("contact_person_date_of_birth", tvContactDob.getText().toString());
        postParam.put("contact_person_date_of_anniversary", tvContactAnni.getText().toString());
        postParam.put("distributor_name", distributor_name);
        postParam.put("image", imgURL);
        Log.d("params>>", postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_STORE_ADD, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                //dialogView.showSingleButtonDialog(StoreAddActivity.this, getResources().getString(R.string.app_name),
                                //response.getString("resp"));

                                CToast.show(StoreAddActivity.this, "New store has been added successfully. Please wait for approval.");
                                GlobalVariable.storeModelArrayList.clear();
                                startActivity(new Intent(StoreAddActivity.this, StoreListActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            } else {
                                dialogView.showSingleButtonDialog(StoreAddActivity.this, getResources().getString(R.string.app_name),
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
                if (error.equals("invalid_credentials")) {
                    Log.e("invalid>>", "You have entered a wrong credentials");
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
        Log.d("json req>>", jsonObjReq.toString());
        // Adding request to request queue
        mQueue.add(jsonObjReq);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StoreAddActivity.this, TaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for blank validation
     *
     * @return boolean
     */
    public boolean blankValidate() {
        if (etStoreName.getText().toString().trim().equals("")) {
            CToast.show(StoreAddActivity.this, "Please enter store name");
            return false;
        } else if (etBusinessName.getText().toString().trim().equals("")) {
            CToast.show(StoreAddActivity.this, "Please enter business name");
            return false;
        } else if (etContact.getText().toString().trim().equals("")) {
            CToast.show(StoreAddActivity.this, "Please enter contact no");
            return false;
        } else if (etContact.getText().toString().length() < 10) {
            CToast.show(StoreAddActivity.this, "Please enter valid contact no");
            return false;
        } else if (etWhatsApp.getText().toString().trim().equals("")) {
            CToast.show(StoreAddActivity.this, "Please enter whatsapp no");
            return false;
        } else if (etWhatsApp.getText().toString().length() < 10) {
            CToast.show(StoreAddActivity.this, "Please enter valid whatsapp no");
            return false;
        } else if (etAddress.getText().toString().trim().equals("")) {
            CToast.show(StoreAddActivity.this, "Please enter address");
            return false;
        } else if (selected_area.equals("") || selected_area.equals("Select Area")) {
            CToast.show(StoreAddActivity.this, "Please choose area");
            return false;
        } else if (etState.getText().toString().trim().equals("")) {
            CToast.show(StoreAddActivity.this, "Please enter state name");
            return false;
        } else if (etPin.getText().toString().trim().equals("")) {
            CToast.show(StoreAddActivity.this, "Please enter pin code");
            return false;
        }
        return true;
    }

    public void getDistributorListTask() {
        dialogView.showCustomSpinProgress(StoreAddActivity.this);
        Map<String, String> postParam = new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_DISTRIBUTOR_LIST + "?ase=" + ase + "&area=" + GlobalVariable.selected_area, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG + "->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray distributorsArray = response.getJSONArray("data");

                                if (distributorsArray.length() > 0) {
                                    for (int i = 0; i < distributorsArray.length(); i++) {
                                        JSONObject distributorObj = distributorsArray.getJSONObject(i);
                                        DistributorModel distributorModel = new DistributorModel();

                                        distributorModel.setId(distributorObj.getString("id"));
                                        distributorModel.setStore_name(distributorObj.getString("store_name"));
                                        distributorModel.setBussiness_name(distributorObj.getString("bussiness_name"));
                                        distributorModel.setStore_OCC_number(distributorObj.getString("store_OCC_number"));
                                        distributorModel.setContact(distributorObj.getString("contact"));
                                        distributorModel.setEmail(distributorObj.getString("email"));
                                        distributorModel.setWhatsapp(distributorObj.getString("whatsapp"));
                                        distributorModel.setAddress(distributorObj.getString("address"));
                                        distributorModel.setArea(distributorObj.getString("area"));
                                        distributorModel.setState(distributorObj.getString("state"));
                                        distributorModel.setCity(distributorObj.getString("city"));
                                        distributorModel.setPin(distributorObj.getString("pin"));
                                        distributorModel.setImage(distributorObj.getString("image"));
                                        distributorModel.setDistributor_id(distributorObj.getString("distributor_id"));

                                        distributorModelArrayList.add(distributorModel);
                                    }
                                }

                                GlobalVariable.distributorModelArrayList = distributorModelArrayList;

                                distributorListAdapter = new DistributorSpinAdapter(GlobalVariable.distributorModelArrayList, StoreAddActivity.this);
                                distributorSpin.setAdapter(distributorListAdapter);

                                getAreaTask();
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

    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals("Take Photo")) {
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (optionsMenu[i].equals("Choose from Gallery")) {
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                } else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(this);
                }

                break;
        }


    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imgStore.setImageBitmap(selectedImage);
                        uploadImage(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imgStore.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                                uploadImage(BitmapFactory.decodeFile(picturePath));
                            }
                        }
                    }
                    break;

            }

        }
    }


    private void uploadImage(Bitmap bitmap) {

        //dialogView.showCustomSpinProgress(this);
        String URL = "https://clever-mendeleev.43-225-53-183.plesk.page/api/store/create/image/upload";

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    //dialogView.dismissCustomSpinProgress();

                    JSONObject obj = new JSONObject(new String(response.data));

                    if (obj.getString("error").equals("false")) {
                        //Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        imgURL = obj.getString("data");

                        //tvPath.setText(imgURL);
                        Log.d("imgURL>>", imgURL);

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    //dialogView.dismissCustomSpinProgress();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(StoreAddActivity.this, "FAILED!!!!", Toast.LENGTH_SHORT).show();
                //dialogView.dismissCustomSpinProgress();

            }
        }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void getAreaTask() {
        dialogView.showCustomSpinProgress(StoreAddActivity.this);
        Map<String, String> postParam = new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_AREA_LIST + "/userid/" + userId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG + "->areas>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray categoriesArray = response.getJSONArray("data");

                                AreaModel categoryModel1 = new AreaModel();

                                categoryModel1.setId("");
                                categoryModel1.setName("Select Area");
                                categoryModel1.setState("");

                                areaModelArrayList.add(categoryModel1);

                                if (categoriesArray.length() > 0) {
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        AreaModel categoryModel = new AreaModel();

                                        categoryModel.setId(categoryObj.getString("id"));
                                        categoryModel.setName(categoryObj.getString("name"));
                                        categoryModel.setState(categoryObj.getString("state"));

                                        areaModelArrayList.add(categoryModel);
                                    }
                                }

                                areaAdapter.notifyDataSetChanged();

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