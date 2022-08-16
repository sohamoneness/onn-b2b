package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.CategoryModel;
import com.b2bapp.onn.model.ProductModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class CatalogueProductAdapter extends BaseAdapter {
    ArrayList<ProductModel> productModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;
    Activity context;

    public CatalogueProductAdapter(Activity activity, ArrayList<ProductModel> productModelArrayList) {
        this.mActivity = activity;
        this.productModelArrayList = productModelArrayList;
        this.inflater = LayoutInflater.from(activity);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mActivity));
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.default_image)
                //.showImageForEmptyUri(R.drawable.default_image)
                // .showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        return productModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ProductModel productModel = productModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_product_catalogue, parent, false);
        }
        TextView txt = (TextView) view.findViewById(R.id.txt);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        TextView code = (TextView) view.findViewById(R.id.code);

        txt.setText(productModel.getName());
        code.setText(productModel.getStyle_no());

        imageLoader.displayImage(WebServices.IMAGE_BASE_URL+productModel.getImage(), img, options);

        return view;
    }
}

