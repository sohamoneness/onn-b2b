package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.ColorAdapter;
import com.b2bapp.onn.adapter.DistributorSizeAdapter;
import com.b2bapp.onn.adapter.ProductListAdapter;
import com.b2bapp.onn.adapter.SizeAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.CategoryModel;
import com.b2bapp.onn.model.CollectionModel;
import com.b2bapp.onn.model.ColorModel;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.model.SizeModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DistributorProductsActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabCategories;
    private ImageView imgBack, imgSearch;
    private LinearLayout liBackGround,liAddProduct;
    private RelativeLayout relProductSizes;
    private Button btnAddToCart, btnCancel;
    private TextView tvProductName, tvStyleCode;
    public static TextView tvCartTotalQty, tvCartTotalAmount;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="ProductListActivity";
    Preferences pref;

    public static String coming_from="";

    ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<CategoryModel>();

    private ListView productList;
    ArrayList<ProductModel> productModelArrayList = new ArrayList<ProductModel>();
    ProductListAdapter productListAdapter;

    private ListView sizeList;
    static ArrayList<SizeModel> sizeModelArrayList = new ArrayList<SizeModel>();
    DistributorSizeAdapter sizeAdapter;

    private Spinner spinColor;
    ArrayList<ColorModel> colorModelArrayList = new ArrayList<ColorModel>();
    ColorAdapter colorAdapter;

    ArrayList<CollectionModel> collectionModelArrayList = new ArrayList<CollectionModel>();

    private ProductModel productModel;

    String size = "";
    String price = "";
    String qty = "";
    String userId = "";

    String collection_id = "";
    String selected_color = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_products);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        userId = pref.getStringPreference(DistributorProductsActivity.this,Preferences.Id);

        tabCategories = (TabLayout) findViewById(R.id.tabCategories);

        //Checking the category list array size in cache
//        if (GlobalVariable.categoryModelArrayList.size()>0){
//            for (int i=0;i< GlobalVariable.categoryModelArrayList.size();i++){
//                tabCategories.addTab(tabCategories.newTab().setText(GlobalVariable.categoryModelArrayList.get(i).getName()));
//            }
//
//            getProductsByCategoryTask(GlobalVariable.categoryModelArrayList.get(0).getId());
//        }else{
//            //Calling getCategoryListTask method
//            getCategoryListTask();
//        }
        liBackGround = (LinearLayout) findViewById(R.id.liBackGround);
        relProductSizes = (RelativeLayout) findViewById(R.id.relProductSizes);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        btnAddToCart = (Button) findViewById(R.id.btnAddToCart);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        imgBack.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
        //liAddProduct.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvStyleCode = (TextView) findViewById(R.id.tvStyleCode);
        tvCartTotalAmount = (TextView) findViewById(R.id.tvCartTotalAmount);
        tvCartTotalQty = (TextView) findViewById(R.id.tvCartTotalQty);

        productList = (ListView) findViewById(R.id.productList);
        productListAdapter = new ProductListAdapter(DistributorProductsActivity.this,productModelArrayList);
        productList.setAdapter(productListAdapter);

//        tabCategories.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int pos = tab.getPosition();
//
//                String categoryId = GlobalVariable.categoryModelArrayList.get(pos).getId();
//                getProductsByCategoryTask(categoryId);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        getCollectionList();
        tabCategories.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                String collectionId = GlobalVariable.collectionModelArrayList.get(pos).getId();
                //getProductsByCategoryTask(categoryId);
                collection_id = collectionId;
                GlobalVariable.choosed_collection_id = collectionId;
                //getCategoryListTask(collectionId);
                getProductsByCategoryTask(collection_id);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //getProductSizesTask();
                productModel = productModelArrayList.get(i);

                tvProductName.setText(productModel.getName());
                tvStyleCode.setText("Style # Of "+productModel.getStyle_no());

                liBackGround.setVisibility(View.VISIBLE);
                relProductSizes.setVisibility(View.VISIBLE);

                //Calling getProductColorTask method
                getProductColorTask(productModel.getId());
            }
        });

        sizeList = (ListView) findViewById(R.id.sizeList);
        sizeAdapter = new DistributorSizeAdapter(DistributorProductsActivity.this,sizeModelArrayList);
        sizeList.setAdapter(sizeAdapter);

        spinColor = (Spinner) findViewById(R.id.spinColor);
        colorAdapter = new ColorAdapter(colorModelArrayList,DistributorProductsActivity.this);
        spinColor.setAdapter(colorAdapter);

        spinColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Calling getProductColorTask method
                selected_color = colorModelArrayList.get(i).getName();
                getProductSizesTask(productModel.getId(),colorModelArrayList.get(i).getCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.imgSearch:
                startActivity(new Intent(DistributorProductsActivity.this, DistributorProductSearchActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            case R.id.liAddProduct:
                liBackGround.setVisibility(View.VISIBLE);
                relProductSizes.setVisibility(View.VISIBLE);
                break;
            case R.id.btnAddToCart:
                int q = 0;
                int p = 0;
                for (int i=0;i<sizeModelArrayList.size();i++){
                    Log.d("size :"+sizeModelArrayList.get(i).getSize(),"qty : "+sizeModelArrayList.get(i).getQty());
                    if (!sizeModelArrayList.get(i).getQty().equals("0") && !sizeModelArrayList.get(i).getQty().equals("")){
                        if (size.equals("")){
                            size += sizeModelArrayList.get(i).getSize();
                        }else{
                            size += "*"+sizeModelArrayList.get(i).getSize();
                        }

                        if (price.equals("")){
                            price += sizeModelArrayList.get(i).getOffer_price();
                        }else{
                            price += "*"+sizeModelArrayList.get(i).getOffer_price();
                        }

                        if (qty.equals("")){
                            qty += sizeModelArrayList.get(i).getQty();
                        }else{
                            qty += "*"+sizeModelArrayList.get(i).getQty();
                        }

                        q = q + Integer.parseInt(sizeModelArrayList.get(i).getQty());
                        p = p + Integer.parseInt(sizeModelArrayList.get(i).getQty())*Integer.parseInt(sizeModelArrayList.get(i).getOffer_price());
                    }

                    tvCartTotalQty.setText(String.valueOf(q)+ " Pcs");
                    tvCartTotalAmount.setText("₹"+String.valueOf(p));
                    Log.d("size>>",size);
                    Log.d("price>>",price);
                    Log.d("qty>>",qty);
                }
                addToCartTask(size,price,qty);
                break;
            case R.id.btnCancel:
                liBackGround.setVisibility(View.GONE);
                relProductSizes.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * This method is for calculating cart amount
     */
    public static void calculateCartAmount(){
        int q = 0;
        int p = 0;
        for (int i=0;i<sizeModelArrayList.size();i++){
            Log.d("size :"+sizeModelArrayList.get(i).getSize(),"qty : "+sizeModelArrayList.get(i).getQty());
            if (!sizeModelArrayList.get(i).getQty().equals("0") && !sizeModelArrayList.get(i).getQty().equals("")){

                q = q + Integer.parseInt(sizeModelArrayList.get(i).getQty());
                p = p + Integer.parseInt(sizeModelArrayList.get(i).getQty())*Integer.parseInt(sizeModelArrayList.get(i).getOffer_price());
            }

            tvCartTotalQty.setText(String.valueOf(q)+ " Pcs");
            tvCartTotalAmount.setText("₹"+String.valueOf(p));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (coming_from.equals("asm")){
            Intent intent = new Intent(DistributorProductsActivity.this, AsmDashboardActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            Intent intent = new Intent(DistributorProductsActivity.this, DistributorDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    public void getCollectionList(){
        dialogView.showCustomSpinProgress(DistributorProductsActivity.this);
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
                                JSONArray categoriesArray = response.getJSONArray("data");

                                if (categoriesArray.length() > 0) {
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        CollectionModel collectionModel = new CollectionModel();

                                        collectionModel.setId(categoryObj.getString("id"));
                                        collectionModel.setName(categoryObj.getString("name"));
                                        collectionModel.setPosition(categoryObj.getString("position"));

                                        collectionModelArrayList.add(collectionModel);
                                    }
                                }

                                Log.d("collection length>>",String.valueOf(collectionModelArrayList.size()));

                                GlobalVariable.collectionModelArrayList = collectionModelArrayList;

                                //Log.d("global collection length>>",String.valueOf(GlobalVariable.collectionModelArrayList.size()));

                                for (int i=0;i< GlobalVariable.collectionModelArrayList.size();i++){
                                    tabCategories.addTab(tabCategories.newTab().setText(GlobalVariable.collectionModelArrayList.get(i).getName()));
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

    /**
     * This method is for fetching category list
     */
    public void getCategoryListTask(){
        dialogView.showCustomSpinProgress(DistributorProductsActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_CATEGORY_LIST, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->categories>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray categoriesArray = response.getJSONArray("data");

                                if (categoriesArray.length() > 0) {
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        CategoryModel categoryModel = new CategoryModel();

                                        categoryModel.setId(categoryObj.getString("id"));
                                        categoryModel.setName(categoryObj.getString("name"));
                                        categoryModel.setParent(categoryObj.getString("parent"));
                                        categoryModel.setPosition(categoryObj.getString("position"));

                                        categoryModelArrayList.add(categoryModel);
                                    }
                                }

                                GlobalVariable.categoryModelArrayList = categoryModelArrayList;

                                for (int i=0;i< GlobalVariable.categoryModelArrayList.size();i++){
                                    tabCategories.addTab(tabCategories.newTab().setText(GlobalVariable.categoryModelArrayList.get(i).getName()));
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

    /**
     * This method is for fetching category wise products
     * @param categoryId
     */
    public void getProductsByCategoryTask(String categoryId){
        dialogView.showCustomSpinProgress(DistributorProductsActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_COLLECTIOB_CATEGORY_WISE_PRODUCTS_LIST+"/"+categoryId+"/products", new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->products>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                productModelArrayList.clear();

                                JSONArray productsArray = response.getJSONArray("data");

                                if (productsArray.length() > 0) {
                                    for (int i = 0; i < productsArray.length(); i++) {
                                        JSONObject productObj = productsArray.getJSONObject(i);
                                        ProductModel productModel = new ProductModel();

                                        productModel.setId(productObj.getString("id"));
                                        productModel.setName(productObj.getString("name"));
                                        productModel.setDesc(productObj.getString("desc"));
                                        productModel.setImage(productObj.getString("image"));
                                        productModel.setPrice(productObj.getString("price"));
                                        productModel.setOffer_price(productObj.getString("offer_price"));
                                        productModel.setPack(productObj.getString("master_pack_count"));
                                        productModel.setStyle_no(productObj.getString("style_no"));
                                        productModel.setSlug(productObj.getString("slug"));

                                        Log.d("GET_PACK>>", productObj.getString("master_pack_count"));

                                        productModelArrayList.add(productModel);
                                    }
                                }

                                productListAdapter.notifyDataSetChanged();
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

    /**
     * This method is for getting product color
     @param productId
     */
    public void getProductColorTask(String productId){
        dialogView.showCustomSpinProgress(DistributorProductsActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_PRODUCT_COLOR+"/"+productId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->colors>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                colorModelArrayList.clear();
                                JSONArray colorsArray = response.getJSONArray("data");

                                if (colorsArray.length() > 0) {
                                    for (int i = 0; i < colorsArray.length(); i++) {
                                        JSONObject colorObj = colorsArray.getJSONObject(i);
                                        ColorModel colorModel = new ColorModel();

                                        //colorModel.setId(colorObj.getString("id"));
                                        colorModel.setName(colorObj.getString("name"));
                                        colorModel.setCode(colorObj.getString("code"));
                                        colorModelArrayList.add(colorModel);
                                    }
                                }

                                colorAdapter.notifyDataSetChanged();

                                //Calling getProductColorTask method
                                getProductSizesTask(productModel.getId(),colorModelArrayList.get(0).getId());
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

    /**
     * This method is for fetching product sizes
     * @param productId
     * @param colorId
     */
    public void getProductSizesTask(String productId, String colorId){
        //dialogView.showCustomSpinProgress(ProductListActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_PRODUCT_SIZE_VIEW+"/"+productId+"/"+colorId, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->sizes>>", response.toString());
                        //dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                sizeModelArrayList.clear();
                                JSONArray sizesArray = response.getJSONArray("data");

                                if (sizesArray.length() > 0) {
                                    for (int i = 0; i < sizesArray.length(); i++) {
                                        JSONObject categoryObj = sizesArray.getJSONObject(i);
                                        SizeModel sizeModel = new SizeModel();

                                        sizeModel.setId(categoryObj.getString("id"));
                                        //sizeModel.setSize(categoryObj.getJSONObject("size_details").getString("name"));
                                        sizeModel.setSize(categoryObj.getString("size_name"));
                                        sizeModel.setPrice(categoryObj.getString("price"));
                                        sizeModel.setDescription(categoryObj.getString("size_details"));
                                        sizeModel.setOffer_price(categoryObj.getString("offer_price"));
                                        sizeModel.setQty("");
                                        sizeModelArrayList.add(sizeModel);
                                    }
                                }

                                sizeAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //dialogView.dismissCustomSpinProgress();
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

    /**
     * This method is for adding product to cart
     * @param size
     * @param price
     * @param qty
     */
    public void addToCartTask(String size, String price, String qty){
        //Log.d("user>>",userId);
        dialogView.showCustomSpinProgress(DistributorProductsActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", userId);
        postParam.put("device_id", userId);
        postParam.put("product_id", productModel.getId());
        postParam.put("product_name", productModel.getName());
        postParam.put("product_style_no", productModel.getStyle_no());
        postParam.put("product_slug", productModel.getSlug());
        postParam.put("product_variation_id", productModel.getId());
        postParam.put("color", selected_color);
        postParam.put("order_type", GlobalVariable.order_type);
        postParam.put("size", size);
        postParam.put("price", price);
        postParam.put("qty", qty);

        Log.d("params>>",postParam.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                WebServices.URL_DISTRIBUTOR_PRODUCT_ADD_CART, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"cart add>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                GlobalVariable.productModel = productModel;
                                startActivity(new Intent(DistributorProductsActivity.this, DistributorReviewActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(DistributorProductsActivity.this, getResources().getString(R.string.app_name),
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