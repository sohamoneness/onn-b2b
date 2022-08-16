package com.b2bapp.onn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.LoginActivity;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.db.DbHelper;
import com.b2bapp.onn.model.AreaModel;
import com.b2bapp.onn.model.CategoryModel;
import com.b2bapp.onn.model.CollectionModel;
import com.b2bapp.onn.model.ColorModel;
import com.b2bapp.onn.model.DistributorModel;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.model.SizeModel;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.model.UserModel;
import com.b2bapp.onn.utils.ConnectionStatus;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="SplashActivity";
    Preferences pref;

    String userId = "";
    String visitId = "";

    String userName = "";

    ArrayList<StoreModel> storeModelArrayList = new ArrayList<StoreModel>();
    ArrayList<AreaModel> areaModelArrayList = new ArrayList<AreaModel>();
    ArrayList<CollectionModel> collectionModelArrayList = new ArrayList<CollectionModel>();
    ArrayList<DistributorModel> distributorModelArrayList = new ArrayList<DistributorModel>();
    ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<CategoryModel>();
    ArrayList<ProductModel> productModelArrayList = new ArrayList<ProductModel>();
    ArrayList<ColorModel> colorModelArrayList = new ArrayList<ColorModel>();
    ArrayList<SizeModel> sizeModelArrayList = new ArrayList<SizeModel>();

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        dbHelper = new DbHelper(SplashActivity.this);

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(SplashActivity.this,Preferences.Id);

        visitId = pref.getStringPreference(SplashActivity.this,Preferences.LAST_VISIT_ID);
        GlobalVariable.selected_area = pref.getStringPreference(SplashActivity.this,Preferences.LAST_SELECTED_AREA);
        userName = pref.getStringPreference(SplashActivity.this,Preferences.User_fname)
                + " "+ pref.getStringPreference(SplashActivity.this,Preferences.User_lname);

        if (pref.getStringPreference(SplashActivity.this,Preferences.Id).equals("")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
            }, 2000);
        }else{
            if (ConnectionStatus.checkConnectionStatus(SplashActivity.this)){
                //Calling getUserDataTask method
                getUserDataTask(userId);
            }else{
//                ArrayList<ProductModel> newProducts = new ArrayList<ProductModel>();
//                newProducts = dbHelper.allProductList();
//
//                for (int i = 0;i<newProducts.size();i++){
//                    Log.d("product_id>>",newProducts.get(i).getId());
//                    Log.d("product_name>>",newProducts.get(i).getName());
//                    Log.d("style_no>>",newProducts.get(i).getStyle_no());
//                }
                ArrayList<ColorModel> newColors = new ArrayList<ColorModel>();
                newColors = dbHelper.allColorList();

                for (int i = 0;i<newColors.size();i++){
                    Log.d("color_name>>",newColors.get(i).getName());
                    Log.d("color_code>>",newColors.get(i).getCode());
                }
                if (visitId.equals("")){
                    startActivity(new Intent(SplashActivity.this, StartingActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, TaskActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
            }

        }
    }

    /**
     * This method is getting user profile data
     * @param userId
     */
    public void getUserDataTask(String userId){
        dialogView.showCustomSpinProgress(SplashActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_USER_PROFILE+"/"+userId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->all data>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONObject userObj = response.getJSONObject("data");

                                UserModel userModel = new UserModel();

                                userModel.setId(userObj.getString("id"));
                                userModel.setFname(userObj.getString("fname"));
                                userModel.setLname(userObj.getString("lname"));
                                userModel.setEmail(userObj.getString("email"));
                                userModel.setMobile(userObj.getString("mobile"));
                                userModel.setWhatsapp_no(userObj.getString("whatsapp_no"));
                                userModel.setEmployee_id(userObj.getString("employee_id"));
                                userModel.setUser_type(userObj.getString("user_type"));
                                userModel.setState(userObj.getString("state"));
                                userModel.setCity(userObj.getString("city"));

                                GlobalVariable.userModel = userModel;

                                pref.storeStringPreference(SplashActivity.this,Preferences.Id,userObj.getString("id"));
                                pref.storeStringPreference(SplashActivity.this,Preferences.User_fname,userObj.getString("fname"));
                                pref.storeStringPreference(SplashActivity.this,Preferences.User_lname,userObj.getString("lname"));
                                pref.storeStringPreference(SplashActivity.this,Preferences.User_Email,userObj.getString("email"));
                                pref.storeStringPreference(SplashActivity.this,Preferences.User_Mobile,userObj.getString("mobile"));
                                pref.storeStringPreference(SplashActivity.this,Preferences.User_Employee_id,userObj.getString("employee_id"));
                                pref.storeStringPreference(SplashActivity.this,Preferences.User_User_type,userObj.getString("user_type"));
                                pref.storeStringPreference(SplashActivity.this,Preferences.User_State,userObj.getString("state"));
                                pref.storeStringPreference(SplashActivity.this,Preferences.User_City,userObj.getString("city"));

                                if (userObj.getString("user_type").equals("1")){
                                    startActivity(new Intent(SplashActivity.this, VpStartingActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("2")){
                                    startActivity(new Intent(SplashActivity.this, RsmDashboardActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("3")){
                                    startActivity(new Intent(SplashActivity.this, AsmDashboardActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("5")){
                                    startActivity(new Intent(SplashActivity.this, DistributorDashboardActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else if (userObj.getString("user_type").equals("4")){
//                                    if (visitId.equals("")){
//                                        startActivity(new Intent(SplashActivity.this, StartingActivity.class));
//                                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//                                        finish();
//                                    }else{
//                                        startActivity(new Intent(SplashActivity.this, TaskActivity.class));
//                                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//                                        finish();
//                                    }
                                    getAreaTask();
                                }
                            }else {
                                dialogView.showSingleButtonDialog(SplashActivity.this, getResources().getString(R.string.app_name),
                                        response.getString("resp"));
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

    public void getAreaTask(){
        dialogView.showCustomSpinProgress(SplashActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_AREA_LIST+"/userid/"+userId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->areas>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray categoriesArray = response.getJSONArray("data");

                                if (categoriesArray.length() > 0) {
                                    dbHelper.deleteAllAreas();
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        AreaModel categoryModel = new AreaModel();

                                        categoryModel.setId(categoryObj.getString("id"));
                                        categoryModel.setName(categoryObj.getString("name"));
                                        categoryModel.setState(categoryObj.getString("state"));
                                        areaModelArrayList.add(categoryModel);

                                        dbHelper.addAreaData(categoryModel);
                                    }
                                }

                                ArrayList<AreaModel> newAreaModelList = new ArrayList<AreaModel>();
                                newAreaModelList = dbHelper.areaList();

                                for (int i=0;i<newAreaModelList.size();i++){
                                    Log.d("id>>",newAreaModelList.get(i).getId());
                                    Log.d("area>>",newAreaModelList.get(i).getName());
                                }

                                getAllData();
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

    public void getAllData(){
        dialogView.showCustomSpinProgress(SplashActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_GET_ALL_DATA+"?ase="+userName, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {

                                JSONArray storesArray = response.getJSONArray("stores");

                                if (storesArray.length() > 0) {
                                    storeModelArrayList.clear();
                                    dbHelper.deleteAllStoreData();
                                    for (int i = 0; i < storesArray.length(); i++) {
                                        JSONObject storeObj = storesArray.getJSONObject(i);
                                        StoreModel storeModel = new StoreModel();

                                        storeModel.setId(storeObj.getString("id"));
                                        storeModel.setStore_name(storeObj.getString("store_name"));
                                        storeModel.setBussiness_name(storeObj.getString("bussiness_name"));
                                        storeModel.setStore_OCC_number(storeObj.getString("store_OCC_number"));
                                        storeModel.setContact(storeObj.getString("contact"));
                                        storeModel.setEmail(storeObj.getString("email"));
                                        storeModel.setWhatsapp(storeObj.getString("whatsapp"));
                                        storeModel.setAddress(storeObj.getString("address"));
                                        storeModel.setArea(storeObj.getString("area"));
                                        storeModel.setState(storeObj.getString("state"));
                                        storeModel.setCity(storeObj.getString("city"));
                                        storeModel.setPin(storeObj.getString("pin"));
                                        storeModel.setImage(storeObj.getString("image"));

                                        storeModelArrayList.add(storeModel);

                                        dbHelper.addStoreData(storeModel);
                                    }
                                }

                                ArrayList<StoreModel> newStores = new ArrayList<StoreModel>();
                                newStores = dbHelper.fetchStoreList();

                                for (int i = 0;i<newStores.size();i++){
                                    Log.d("store_id>>",newStores.get(i).getId());
                                    Log.d("store_name>>",newStores.get(i).getStore_name());
                                    Log.d("store_area>>",newStores.get(i).getArea());
                                }

                                JSONArray collectionsArray = response.getJSONArray("collections");

                                if (collectionsArray.length() > 0) {
                                    dbHelper.deleteAllCollections();
                                    for (int i = 0; i < collectionsArray.length(); i++) {
                                        JSONObject categoryObj = collectionsArray.getJSONObject(i);
                                        CollectionModel collectionModel = new CollectionModel();

                                        collectionModel.setId(categoryObj.getString("id"));
                                        collectionModel.setName(categoryObj.getString("name"));
                                        collectionModel.setPosition(categoryObj.getString("position"));

                                        collectionModelArrayList.add(collectionModel);

                                        dbHelper.addCollectionData(collectionModel);
                                    }
                                }

                                ArrayList<CollectionModel> newCollections = new ArrayList<CollectionModel>();
                                newCollections = dbHelper.collectionList();

                                for (int i = 0;i<newCollections.size();i++){
                                    Log.d("collection_id>>",newCollections.get(i).getId());
                                    Log.d("collection_name>>",newCollections.get(i).getName());
                                }

                                JSONArray distributorsArray = response.getJSONArray("distributors");

                                if (distributorsArray.length() > 0) {
                                    dbHelper.deleteAllDistributors();
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

                                        dbHelper.addDistributorData(distributorModel);
                                    }
                                }

                                ArrayList<DistributorModel> newDistributors = new ArrayList<DistributorModel>();
                                newDistributors = dbHelper.fetchDistributorList();

                                for (int i = 0;i<newDistributors.size();i++){
                                    Log.d("distributor_name>>",newDistributors.get(i).getBussiness_name());
                                    Log.d("distributor_area>>",newDistributors.get(i).getArea());
                                }

                                JSONArray categoriesArray = response.getJSONArray("categories");

                                if (categoriesArray.length() > 0) {
                                    dbHelper.deleteAllCategories();
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        CategoryModel categoryModel = new CategoryModel();

                                        categoryModel.setId(categoryObj.getString("id"));
                                        categoryModel.setName(categoryObj.getString("name"));
                                        categoryModel.setParent(categoryObj.getString("parent"));

                                        categoryModelArrayList.add(categoryModel);

                                        dbHelper.addCategoryData(categoryModel);
                                    }
                                }

                                ArrayList<CategoryModel> newCategories = new ArrayList<CategoryModel>();
                                newCategories = dbHelper.categoryList();

                                for (int i = 0;i<newCategories.size();i++){
                                    Log.d("category_id>>",newCategories.get(i).getId());
                                    Log.d("category_name>>",newCategories.get(i).getName());
                                    Log.d("category_parent>>",newCategories.get(i).getParent());
                                }

                                JSONArray productsArray = response.getJSONArray("products");

                                if (productsArray.length() > 0) {
                                    dbHelper.deleteAllProducts();
                                    for (int i = 0; i < productsArray.length(); i++) {
                                        JSONObject productObj = productsArray.getJSONObject(i);
                                        ProductModel productModel = new ProductModel();

                                        productModel.setId(productObj.getString("id"));
                                        productModel.setName(productObj.getString("name"));
                                        productModel.setImage(productObj.getString("image"));
                                        productModel.setStyle_no(productObj.getString("style_no"));
                                        productModel.setMaster_pack_count(productObj.getString("master_pack_count"));
                                        productModel.setCollection_id(productObj.getString("collection_id"));
                                        productModel.setCategory_id(productObj.getString("cat_id"));

                                        productModelArrayList.add(productModel);

                                        dbHelper.addProductData(productModel);
                                    }
                                }

                                ArrayList<ProductModel> newProducts = new ArrayList<ProductModel>();
                                newProducts = dbHelper.allProductList();

                                for (int i = 0;i<newProducts.size();i++){
                                    Log.d("product_id>>",newProducts.get(i).getId());
                                    Log.d("product_name>>",newProducts.get(i).getName());
                                    Log.d("style_no>>",newProducts.get(i).getStyle_no());
                                }

                                JSONArray colorsArray = response.getJSONArray("colors");

                                if (colorsArray.length() > 0) {
                                    dbHelper.deleteAllColors();
                                    for (int i = 0; i < colorsArray.length(); i++) {
                                        JSONObject colorObj = colorsArray.getJSONObject(i);
                                        ColorModel colorModel = new ColorModel();

                                        colorModel.setName(colorObj.getString("color_name"));
                                        colorModel.setCode(colorObj.getString("color"));
                                        colorModel.setProduct_id(colorObj.getString("product_id"));
                                        colorModelArrayList.add(colorModel);

                                        dbHelper.addColorData(colorModel);
                                    }
                                }

                                ArrayList<ColorModel> newColors = new ArrayList<ColorModel>();
                                newColors = dbHelper.allColorList();

                                for (int i = 0;i<newColors.size();i++){
                                    Log.d("color_name>>",newColors.get(i).getName());
                                    Log.d("color_code>>",newColors.get(i).getCode());
                                }

                                dbHelper.deleteAllSizes();

                                SizeModel sizeModel1 = new SizeModel();
                                sizeModel1.setSize("Free Size");
                                sizeModel1.setQty("");
                                sizeModel1.setPrice("0");
                                sizeModel1.setOffer_price("0");
                                dbHelper.addSizeData(sizeModel1);
                                SizeModel sizeModel2 = new SizeModel();
                                sizeModel2.setSize("XS");
                                sizeModel2.setQty("");
                                sizeModel2.setPrice("0");
                                sizeModel2.setOffer_price("0");
                                dbHelper.addSizeData(sizeModel2);
                                SizeModel sizeModel3 = new SizeModel();
                                sizeModel3.setSize("S");
                                sizeModel3.setQty("");
                                sizeModel3.setPrice("0");
                                sizeModel3.setOffer_price("0");
                                dbHelper.addSizeData(sizeModel3);
                                SizeModel sizeModel4 = new SizeModel();
                                sizeModel4.setSize("M");
                                sizeModel4.setQty("");
                                sizeModel4.setPrice("0");
                                sizeModel4.setOffer_price("0");
                                dbHelper.addSizeData(sizeModel4);
                                SizeModel sizeModel5 = new SizeModel();
                                sizeModel5.setSize("XL");
                                sizeModel5.setQty("");
                                sizeModel5.setPrice("0");
                                sizeModel5.setOffer_price("0");
                                dbHelper.addSizeData(sizeModel5);
                                SizeModel sizeModel6 = new SizeModel();
                                sizeModel6.setSize("XXL");
                                sizeModel6.setQty("");
                                sizeModel6.setPrice("0");
                                sizeModel6.setOffer_price("0");
                                dbHelper.addSizeData(sizeModel6);
                                SizeModel sizeModel7 = new SizeModel();
                                sizeModel7.setSize("XXXL");
                                sizeModel7.setQty("");
                                sizeModel7.setPrice("0");
                                sizeModel7.setOffer_price("0");
                                dbHelper.addSizeData(sizeModel7);

//                                JSONArray sizesArray = response.getJSONArray("color_sizes");
//
//                                if (sizesArray.length() > 0) {
//                                    dbHelper.deleteAllSizes();
//                                    for (int i = 0; i < sizesArray.length(); i++) {
//                                        JSONObject categoryObj = sizesArray.getJSONObject(i);
//                                        SizeModel sizeModel = new SizeModel();
//
//                                        sizeModel.setId(categoryObj.getString("id"));
//                                        //sizeModel.setSize(categoryObj.getJSONObject("size_details").getString("name"));
//                                        sizeModel.setSize(categoryObj.getString("size_name"));
//                                        sizeModel.setPrice(categoryObj.getString("price"));
//                                        sizeModel.setDescription(categoryObj.getString("size_details"));
//                                        sizeModel.setOffer_price(categoryObj.getString("offer_price"));
//                                        sizeModel.setQty("");
//                                        sizeModel.setProduct_id(categoryObj.getString("product_id"));
//                                        sizeModel.setColor_code(categoryObj.getString("color"));
//                                        sizeModelArrayList.add(sizeModel);
//
//                                        dbHelper.addSizeData(sizeModel);
//                                    }
//                                }

                                ArrayList<SizeModel> newSizes = new ArrayList<SizeModel>();
                                newSizes = dbHelper.allSizeList();

                                for (int i = 0;i<newSizes.size();i++){
                                    Log.d("size>>",newSizes.get(i).getSize());
                                    Log.d("description>>",newSizes.get(i).getDescription());
                                }

                                if (visitId.equals("")){
                                    startActivity(new Intent(SplashActivity.this, StartingActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }else{
                                    startActivity(new Intent(SplashActivity.this, TaskActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
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