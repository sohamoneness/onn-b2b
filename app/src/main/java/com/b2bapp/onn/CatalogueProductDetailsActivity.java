package com.b2bapp.onn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.utils.DialogView;
import com.b2bapp.onn.utils.Preferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CatalogueProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static ProductModel productModel;
    private TextView tvTitle, tvProductTitle, tvProductPrice, tvProductStyle, tvProductDescription;
    private ImageView imgProduct;
    private ImageView imgBack;

    RequestQueue mQueue;
    DialogView dialogView;
    private String TAG="CatalogueProductDetailsActivity";
    Preferences pref;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_product_details);

        getSupportActionBar().hide();

        pref=new Preferences(this);
        dialogView=new DialogView();
        mQueue = Volley.newRequestQueue(this);

        imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(this);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(CatalogueProductDetailsActivity.this));
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.default_image)
                //.showImageForEmptyUri(R.drawable.default_image)
                // .showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvProductTitle = (TextView) findViewById(R.id.tvProductTitle);
        tvProductPrice = (TextView) findViewById(R.id.tvProductPrice);
        tvProductStyle = (TextView) findViewById(R.id.tvProductStyle);
        tvProductDescription = (TextView) findViewById(R.id.tvProductDescription);

        imgProduct = (ImageView) findViewById(R.id.imgProduct);

        imageLoader.displayImage(WebServices.IMAGE_BASE_URL+productModel.getImage(), imgProduct, options);

        tvTitle.setText(productModel.getName());
        tvProductTitle.setText(productModel.getName());
        tvProductPrice.setText("Rs. "+productModel.getPrice());
        tvProductStyle.setText("Style : "+productModel.getStyle_no());
        tvProductDescription.setText(productModel.getDesc());
        tvProductDescription.setSingleLine(false);
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
        Intent intent = new Intent(CatalogueProductDetailsActivity.this, CatalogueProductlistActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}