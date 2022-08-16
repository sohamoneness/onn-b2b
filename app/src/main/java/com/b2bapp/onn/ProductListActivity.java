package com.b2bapp.onn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.b2bapp.onn.adapter.ProductListAdapter;
import com.b2bapp.onn.adapter.RvCategoryAdapter;
import com.b2bapp.onn.adapter.SizeAdapter;
import com.b2bapp.onn.adapter.StorelistAdapter;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.db.DbHelper;
import com.b2bapp.onn.model.CartModel;
import com.b2bapp.onn.model.CategoryModel;
import com.b2bapp.onn.model.CollectionModel;
import com.b2bapp.onn.model.ColorModel;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.model.SizeModel;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.utils.CToast;
import com.b2bapp.onn.utils.ConnectionStatus;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProductListActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabCategories;
    private ImageView imgBack, imgSearch, imgCart;
    private LinearLayout liBackGround,liAddProduct;
    private RelativeLayout relProductSizes;
    private Button btnAddToCart, btnCancel;
    private TextView tvProductName, tvStyleCode;
    public static TextView tvCartTotalQty, tvCartTotalAmount;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="ProductListActivity";
    Preferences pref;

    static RequestQueue mQueue1;
    static DialogView dialogView1;
    private String TAG1="ProductListActivity";

    ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<CategoryModel>();

    ArrayList<CollectionModel> collectionModelArrayList = new ArrayList<CollectionModel>();

    private ListView productList;
    static ArrayList<ProductModel> productModelArrayList = new ArrayList<ProductModel>();
    ProductListAdapter productListAdapter;

    private ListView sizeList;
    static ArrayList<SizeModel> sizeModelArrayList = new ArrayList<SizeModel>();
    SizeAdapter sizeAdapter;

    private Spinner spinColor;
    ArrayList<ColorModel> colorModelArrayList = new ArrayList<ColorModel>();
    ColorAdapter colorAdapter;

    private ProductModel productModel;

    String size = "";
    String price = "";
    String qty = "";
    String userId = "";
    String userType = "";

    private RecyclerView rvCategories;
    private String[] serviceName;
    LinearLayoutManager layoutManagerServiceList;
    RvCategoryAdapter rvCategoryAdapter;

    String collection_id = "";
    String selected_color = "";

    DbHelper dbHelper;

    public static String coming_from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        getSupportActionBar().hide();

        dbHelper = new DbHelper(ProductListActivity.this);

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        dialogView1=new DialogView();
        mQueue1 = Volley.newRequestQueue(this);

        userType = pref.getStringPreference(ProductListActivity.this,Preferences.User_User_type);

        if (userType.equals("3")){
            userId = GlobalVariable.ase_id_from_asm_dashboard;
        }else{
            userId = pref.getStringPreference(ProductListActivity.this,Preferences.Id);
        }

        tabCategories = (TabLayout) findViewById(R.id.tabCategories);

        liBackGround = (LinearLayout) findViewById(R.id.liBackGround);
        relProductSizes = (RelativeLayout) findViewById(R.id.relProductSizes);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        imgCart = (ImageView) findViewById(R.id.imgCart);

        btnAddToCart = (Button) findViewById(R.id.btnAddToCart);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        imgBack.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
        //liAddProduct.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imgCart.setOnClickListener(this);

        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvStyleCode = (TextView) findViewById(R.id.tvStyleCode);
        tvCartTotalAmount = (TextView) findViewById(R.id.tvCartTotalAmount);
        tvCartTotalQty = (TextView) findViewById(R.id.tvCartTotalQty);

        productList = (ListView) findViewById(R.id.productList);
        productListAdapter = new ProductListAdapter(ProductListActivity.this,productModelArrayList);
        productList.setAdapter(productListAdapter);

        rvCategories = (RecyclerView) findViewById(R.id.rvCategories);
        layoutManagerServiceList = new LinearLayoutManager(ProductListActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(layoutManagerServiceList);
        rvCategoryAdapter = new RvCategoryAdapter(ProductListActivity.this,categoryModelArrayList,serviceName);
        rvCategories.setAdapter(rvCategoryAdapter);

        //Checking the category list array size in cache
        if (GlobalVariable.collectionModelArrayList.size()>0){
            for (int i=0;i< GlobalVariable.collectionModelArrayList.size();i++){
                tabCategories.addTab(tabCategories.newTab().setText(GlobalVariable.collectionModelArrayList.get(i).getName()));
            }

            //getProductsByCategoryTask(GlobalVariable.categoryModelArrayList.get(0).getId());
        }else{
            if (ConnectionStatus.checkConnectionStatus(ProductListActivity.this)){
                //Calling getCategoryListTask method
                getCollectionList();
            }else{
                ArrayList<CollectionModel> collectionModelArrayList2 = new ArrayList<CollectionModel>();
                collectionModelArrayList2 = dbHelper.collectionList();
                GlobalVariable.collectionModelArrayList = collectionModelArrayList2;

                //Log.d("global collection length>>",String.valueOf(GlobalVariable.collectionModelArrayList.size()));

                for (int i=0;i< GlobalVariable.collectionModelArrayList.size();i++){
                    tabCategories.addTab(tabCategories.newTab().setText(GlobalVariable.collectionModelArrayList.get(i).getName()));
                }

                categoryModelArrayList = dbHelper.collectionWiseCategoryList(GlobalVariable.collectionModelArrayList.get(0).getId());
                //categoryModelArrayList = dbHelper.categoryList();
                for (int i=0;i<categoryModelArrayList.size();i++){
                    Log.d("category>>",categoryModelArrayList.get(i).getName());
                }
                rvCategoryAdapter = new RvCategoryAdapter(ProductListActivity.this,categoryModelArrayList,serviceName);
                rvCategories.setAdapter(rvCategoryAdapter);

                productModelArrayList = dbHelper.filterProductList(GlobalVariable.collectionModelArrayList.get(0).getId(),categoryModelArrayList.get(0).getId());
                Log.d("product12>>",String.valueOf(productModelArrayList.size()));
                productListAdapter = new ProductListAdapter(ProductListActivity.this,productModelArrayList);
                productList.setAdapter(productListAdapter);
            }

        }

        tabCategories.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                String collectionId = GlobalVariable.collectionModelArrayList.get(pos).getId();
                //getProductsByCategoryTask(categoryId);
                collection_id = collectionId;
                GlobalVariable.choosed_collection_id = collectionId;
                Log.d("collection_id>>",collection_id);

                if (ConnectionStatus.checkConnectionStatus(ProductListActivity.this)){
                    getCategoryListTask(collectionId);
                }else{
                    categoryModelArrayList = dbHelper.collectionWiseCategoryList(collection_id);
                    //categoryModelArrayList = dbHelper.categoryList();
                    for (int i=0;i<categoryModelArrayList.size();i++){
                        Log.d("category>>",categoryModelArrayList.get(i).getName());
                    }
                    rvCategoryAdapter = new RvCategoryAdapter(ProductListActivity.this,categoryModelArrayList,serviceName);
                    rvCategories.setAdapter(rvCategoryAdapter);
                }

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

                GlobalVariable.pack_count = productModel.getMaster_pack_count();

                tvProductName.setText(productModel.getName());
                tvStyleCode.setText("Style # Of "+productModel.getStyle_no());

                liBackGround.setVisibility(View.VISIBLE);
                relProductSizes.setVisibility(View.VISIBLE);

                if (ConnectionStatus.checkConnectionStatus(ProductListActivity.this)){
                    //Calling getProductColorTask method
                    getProductColorTask(productModel.getId());
                }else{
                    colorModelArrayList = dbHelper.filterColorList(productModel.getId());
                    colorAdapter = new ColorAdapter(colorModelArrayList,ProductListActivity.this);
                    spinColor.setAdapter(colorAdapter);
                    getProductSizesTask(productModel.getId(),colorModelArrayList.get(0).getCode());
//                    if (ConnectionStatus.checkConnectionStatus(ProductListActivity.this)){
//                        //Calling getProductColorTask method
//
//                    }else{
//                        sizeModelArrayList = dbHelper.allSizeList();
//                        sizeAdapter = new SizeAdapter(ProductListActivity.this,sizeModelArrayList);
//                        sizeList.setAdapter(sizeAdapter);
//                    }

                }

            }
        });

        sizeList = (ListView) findViewById(R.id.sizeList);
        sizeAdapter = new SizeAdapter(ProductListActivity.this,sizeModelArrayList);
        sizeList.setAdapter(sizeAdapter);

        spinColor = (Spinner) findViewById(R.id.spinColor);
        colorAdapter = new ColorAdapter(colorModelArrayList,ProductListActivity.this);
        spinColor.setAdapter(colorAdapter);

        spinColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Calling getProductColorTask method
                selected_color = colorModelArrayList.get(i).getName();

                if (ConnectionStatus.checkConnectionStatus(ProductListActivity.this)){
                    getProductSizesTask(productModel.getId(),colorModelArrayList.get(i).getCode());
                }else{
                    sizeModelArrayList = dbHelper.allSizeList();
                    sizeAdapter = new SizeAdapter(ProductListActivity.this,sizeModelArrayList);
                    sizeList.setAdapter(sizeAdapter);
                }

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
                startActivity(new Intent(ProductListActivity.this, SearchActivity.class));
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

                if (ConnectionStatus.checkConnectionStatus(ProductListActivity.this)){
                    int quantity_pass = 1;

                    for (int i=0;i<sizeModelArrayList.size();i++){
                        Log.d("size :"+sizeModelArrayList.get(i).getSize(),"qty : "+sizeModelArrayList.get(i).getQty());
                        if (!sizeModelArrayList.get(i).getQty().equals("0") && !sizeModelArrayList.get(i).getQty().equals("")){
//                        if (Integer.parseInt(GlobalVariable.pack_count)>Integer.parseInt(sizeModelArrayList.get(i).getQty())){
//                            quantity_pass = 0;
//                            break;
//                        }

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

                    if (quantity_pass==1){
                        addToCartTask(size,price,qty);
                    }else{
                        q = 0;
                        p= 0;
                        CToast.show(ProductListActivity.this,"Minimum order quantity should be : "+GlobalVariable.pack_count);
                    }
                }else{
                    for (int i=0;i<sizeModelArrayList.size();i++){
                        Log.d("size :"+sizeModelArrayList.get(i).getSize(),"qty : "+sizeModelArrayList.get(i).getQty());
                        if (!sizeModelArrayList.get(i).getQty().equals("0") && !sizeModelArrayList.get(i).getQty().equals("")){
//                        if (Integer.parseInt(GlobalVariable.pack_count)>Integer.parseInt(sizeModelArrayList.get(i).getQty())){
//                            quantity_pass = 0;
//                            break;
//                        }

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

                            CartModel cartModel = new CartModel();
                            Random random = new Random();
                            int number = random.nextInt(100000);
                            cartModel.setId(String.valueOf(number));
                            cartModel.setProduct_name(productModel.getName());
                            cartModel.setProduct_style(productModel.getStyle_no());
                            cartModel.setColor(selected_color);
                            cartModel.setSize(sizeModelArrayList.get(i).getSize());
                            cartModel.setQty(sizeModelArrayList.get(i).getQty());
                            cartModel.setOffer_price("0");
                            dbHelper.addCartData(cartModel);

                            q = q + Integer.parseInt(sizeModelArrayList.get(i).getQty());
                            p = p + Integer.parseInt(sizeModelArrayList.get(i).getQty())*Integer.parseInt(sizeModelArrayList.get(i).getOffer_price());
                        }



                        tvCartTotalQty.setText(String.valueOf(q)+ " Pcs");
                        tvCartTotalAmount.setText("₹"+String.valueOf(p));
                        Log.d("size>>",size);
                        Log.d("price>>",price);
                        Log.d("qty>>",qty);

                        GlobalVariable.productModel = productModel;
                        startActivity(new Intent(ProductListActivity.this, ReviewOrderActivity.class));
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }

                }


                break;
            case R.id.btnCancel:
                liBackGround.setVisibility(View.GONE);
                relProductSizes.setVisibility(View.GONE);
                break;
            case R.id.imgCart:
                GlobalVariable.productModel = productModel;
                startActivity(new Intent(ProductListActivity.this, ReviewOrderActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
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
            Intent intent = new Intent(ProductListActivity.this, AsmDashboardActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            Intent intent = new Intent(ProductListActivity.this, StoreDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public static void callProductList(){
        new ProductListActivity().getProductsByCategoryTask(GlobalVariable.category_id);
    }

    public void getCollectionList(){
        dialogView.showCustomSpinProgress(ProductListActivity.this);
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
//    public void getCategoryListTask(){
//        dialogView.showCustomSpinProgress(ProductListActivity.this);
//        Map<String, String> postParam= new HashMap<String, String>();
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
//                WebServices.URL_CATEGORY_LIST, new JSONObject(postParam),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG+"->categories>>", response.toString());
//                        dialogView.dismissCustomSpinProgress();
//                        try {
//                            if (!response.getBoolean("error")) {
//                                JSONArray categoriesArray = response.getJSONArray("data");
//
//                                if (categoriesArray.length() > 0) {
//                                    for (int i = 0; i < categoriesArray.length(); i++) {
//                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
//                                        CategoryModel categoryModel = new CategoryModel();
//
//                                        categoryModel.setId(categoryObj.getString("id"));
//                                        categoryModel.setName(categoryObj.getString("name"));
//                                        categoryModel.setParent(categoryObj.getString("parent"));
//                                        categoryModel.setPosition(categoryObj.getString("position"));
//
//                                        categoryModelArrayList.add(categoryModel);
//                                    }
//                                }
//
//                                GlobalVariable.categoryModelArrayList = categoryModelArrayList;
//
//                                for (int i=0;i< GlobalVariable.categoryModelArrayList.size();i++){
//                                    tabCategories.addTab(tabCategories.newTab().setText(GlobalVariable.categoryModelArrayList.get(i).getName()));
//                                }
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                dialogView.dismissCustomSpinProgress();
//            }
//        }) {
//            /**
//             * Passing some request headers
//             * */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                return headers;
//            }
//        };
//
//        jsonObjReq.setTag(TAG);
//        // Adding request to request queue
//        mQueue.add(jsonObjReq);
//    }

    public void getCategoryListTask(String collectionId){
        dialogView.showCustomSpinProgress(ProductListActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_COLLECTION_LIST+"/"+collectionId+"/category", new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->categories>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                categoryModelArrayList.clear();
                                JSONArray categoriesArray = response.getJSONArray("data");

                                if (categoriesArray.length() > 0) {
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                        CategoryModel categoryModel = new CategoryModel();

                                        categoryModel.setId(categoryObj.getString("id"));
                                        categoryModel.setName(categoryObj.getString("name"));
                                        //categoryModel.setParent(categoryObj.getString("parent"));
                                        //categoryModel.setPosition(categoryObj.getString("position"));

                                        categoryModelArrayList.add(categoryModel);
                                    }
                                }

//                                GlobalVariable.categoryModelArrayList = categoryModelArrayList;
//
//                                for (int i=0;i< GlobalVariable.categoryModelArrayList.size();i++){
//                                    tabCategories.addTab(tabCategories.newTab().setText(GlobalVariable.categoryModelArrayList.get(i).getName()));
//                                }
                                rvCategoryAdapter.notifyDataSetChanged();
                                getProductsByCategoryTask(categoryModelArrayList.get(0).getId());
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
        if (ConnectionStatus.checkConnectionStatus(ProductListActivity.this)){
            //dialogView.showCustomSpinProgress(ProductListActivity.this);
            Map<String, String> postParam= new HashMap<String, String>();

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    WebServices.URL_COLLECTIOB_CATEGORY_WISE_PRODUCTS_LIST+"/"+collection_id+"/category/"+categoryId, new JSONObject(postParam),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG+"->products>>", response.toString());
                            //dialogView.dismissCustomSpinProgress();
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
                                            //productModel.setDesc(productObj.getString("desc"));
                                            productModel.setImage(productObj.getString("image"));
                                            //productModel.setPrice(productObj.getString("price"));
                                            //productModel.setOffer_price(productObj.getString("offer_price"));
                                            //productModel.setPack(productObj.getString("pack"));
                                            productModel.setStyle_no(productObj.getString("style_no"));
                                            productModel.setMaster_pack_count(productObj.getString("master_pack_count"));
                                            //productModel.setSlug(productObj.getString("slug"));

                                            productModelArrayList.add(productModel);
                                        }
                                    }

                                    //productListAdapter.notifyDataSetChanged();
                                    productList.setAdapter(productListAdapter);
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
        }else{
            Log.d("product11>>","I am in");
            //productModelArrayList = dbHelper.allProductList();
            productModelArrayList = dbHelper.filterProductList(collection_id,categoryId);
            Log.d("product12>>",String.valueOf(productModelArrayList.size()));
            productListAdapter = new ProductListAdapter(ProductListActivity.this,productModelArrayList);
            productList.setAdapter(productListAdapter);
        }

    }

    /**
     * This method is for getting product color
     @param productId
     */
    public void getProductColorTask(String productId){
        dialogView.showCustomSpinProgress(ProductListActivity.this);
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
                                getProductSizesTask(productModel.getId(),colorModelArrayList.get(0).getCode());
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
        dialogView.showCustomSpinProgress(ProductListActivity.this);
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
                WebServices.URL_PRODUCT_ADD_CART, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"cart add>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                GlobalVariable.productModel = productModel;
                                startActivity(new Intent(ProductListActivity.this, ReviewOrderActivity.class));
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }else {
                                dialogView.showSingleButtonDialog(ProductListActivity.this, getResources().getString(R.string.app_name),
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