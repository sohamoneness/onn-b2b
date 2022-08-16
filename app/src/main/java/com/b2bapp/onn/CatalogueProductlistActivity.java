package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.b2bapp.onn.adapter.CatalogueProductAdapter;
import com.b2bapp.onn.adapter.CatelogueCategoryAdapter;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.customView.ExpandableHeightGridView;
import com.b2bapp.onn.model.CategoryModel;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CatalogueProductlistActivity extends AppCompatActivity implements View.OnClickListener {

    private ExpandableHeightGridView gvProducts;
    private ImageView imgBack;
    private TextView tvTitle;

    ArrayList<ProductModel> productModelArrayList = new ArrayList<ProductModel>();
    CatalogueProductAdapter catalogueProductAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="CatalogueProductlistActivity";
    Preferences pref;

    public static CategoryModel categoryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_productlist);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(categoryModel.getName());

        imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(this);

        gvProducts = (ExpandableHeightGridView) findViewById(R.id.gvProducts);
        catalogueProductAdapter = new CatalogueProductAdapter(CatalogueProductlistActivity.this,productModelArrayList);
        gvProducts.setAdapter(catalogueProductAdapter);

        //Calling getProductsByCategoryTask method
        getProductsByCategoryTask(categoryModel.getId());

        //On selecting product get the product id
        gvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CatalogueProductDetailsActivity.productModel = productModelArrayList.get(i);
                startActivity(new Intent(CatalogueProductlistActivity.this, CatalogueProductDetailsActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    /**
     * This method is for fetching category wise products
     * @param categoryId
     */
    public void getProductsByCategoryTask(String categoryId){
        dialogView.showCustomSpinProgress(CatalogueProductlistActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_CATEGORY_WISE_PRODUCTS_LIST+"/"+categoryId+"/products", new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->products>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {

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
                                        productModel.setPack(productObj.getString("pack"));
                                        productModel.setStyle_no(productObj.getString("style_no"));
                                        productModel.setSlug(productObj.getString("slug"));

                                        productModelArrayList.add(productModel);
                                    }
                                }

                                catalogueProductAdapter.notifyDataSetChanged();
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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CatalogueProductlistActivity.this, ProductCatalogueActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}