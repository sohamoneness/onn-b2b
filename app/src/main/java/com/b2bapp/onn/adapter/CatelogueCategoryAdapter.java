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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class CatelogueCategoryAdapter extends BaseAdapter {
    ArrayList<CategoryModel> categoryModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;
    Activity context;

    public CatelogueCategoryAdapter(Activity activity, ArrayList<CategoryModel> categoryModelArrayList) {
        this.mActivity = activity;
        this.categoryModelArrayList = categoryModelArrayList;
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
        return categoryModelArrayList.size();
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
        CategoryModel categoryModel = categoryModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_category_catelogue, parent, false);
        }
        TextView txt = (TextView) view.findViewById(R.id.txt);
        ImageView img = (ImageView) view.findViewById(R.id.img);

        txt.setText(categoryModel.getName());

        imageLoader.displayImage(WebServices.IMAGE_BASE_URL+categoryModel.getIcon(), img, options);

        return view;
    }
}
