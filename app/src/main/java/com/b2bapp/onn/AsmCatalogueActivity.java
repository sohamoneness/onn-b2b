package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.adapter.CatalogueAdapter;
import com.b2bapp.onn.adapter.CatelogueCategoryAdapter;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.customView.ExpandableHeightGridView;
import com.b2bapp.onn.model.CatalogueModel;
import com.b2bapp.onn.model.CategoryModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AsmCatalogueActivity extends AppCompatActivity implements View.OnClickListener {

    private ExpandableHeightGridView gvCategories;
    private ImageView imgBack;

    ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<CategoryModel>();
    CatelogueCategoryAdapter catelogueCategoryAdapter;

    ArrayList<CatalogueModel> catalogueModelArrayList = new ArrayList<CatalogueModel>();
    CatalogueAdapter catalogueAdapter;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="DistributorActivity";
    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asm_catalogue);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(this);

        gvCategories = (ExpandableHeightGridView) findViewById(R.id.gvCategories);
//        catelogueCategoryAdapter = new CatelogueCategoryAdapter(ProductCatalogueActivity.this,categoryModelArrayList);
//        gvCategories.setAdapter(catelogueCategoryAdapter);

        catalogueAdapter = new CatalogueAdapter(AsmCatalogueActivity.this, catalogueModelArrayList);
        gvCategories.setAdapter(catalogueAdapter);

        //On selecting category get the category id
        gvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                CatalogueProductlistActivity.categoryModel = categoryModelArrayList.get(i);
//                startActivity(new Intent(ProductCatalogueActivity.this, CatalogueProductlistActivity.class));
//                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//                finish();

                String url = WebServices.IMAGE_BASE_URL+catalogueModelArrayList.get(i).getPdf();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        //Calling getCategoryListTask method
        //getCategoryListTask();
        getCatalogueTask();
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
        Intent intent = new Intent(AsmCatalogueActivity.this, AsmDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method is for getting catalogue list
     */
    public void getCatalogueTask(){
        dialogView.showCustomSpinProgress(AsmCatalogueActivity.this);
        Map<String, String> postParam= new HashMap<String, String>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                WebServices.URL_CATALOGUE_LIST, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"->tickets>>", response.toString());
                        dialogView.dismissCustomSpinProgress();
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray offersArray = response.getJSONArray("data");

                                if (offersArray.length() > 0) {
                                    for (int i = 0; i < offersArray.length(); i++) {
                                        JSONObject offerObj = offersArray.getJSONObject(i);
                                        CatalogueModel schemeModel = new CatalogueModel();

                                        schemeModel.setId(offerObj.getString("id"));
                                        schemeModel.setImage(offerObj.getString("image"));
                                        schemeModel.setPdf(offerObj.getString("pdf"));
                                        schemeModel.setTitle(offerObj.getString("title"));

                                        catalogueModelArrayList.add(schemeModel);
                                    }
                                }

                                catalogueAdapter.notifyDataSetChanged();
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