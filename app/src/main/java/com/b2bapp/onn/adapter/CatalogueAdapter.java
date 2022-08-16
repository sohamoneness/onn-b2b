package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.base.WebServices;
import com.b2bapp.onn.model.CatalogueModel;
import com.b2bapp.onn.model.SchemeModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class CatalogueAdapter extends BaseAdapter {
    ArrayList<CatalogueModel> catalogueModelArrayList;
    Activity mActivity;
    LayoutInflater inflater;
    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;
    Activity context;

    public CatalogueAdapter(Activity activity, ArrayList<CatalogueModel> catalogueModelArrayList) {
        this.mActivity = activity;
        this.catalogueModelArrayList = catalogueModelArrayList;
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
        return catalogueModelArrayList.size();
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
        CatalogueModel catalogueModel = catalogueModelArrayList.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.inflate_catalogue, parent, false);
        }
        ImageView img = (ImageView) view.findViewById(R.id.img);

        imageLoader.displayImage(WebServices.IMAGE_BASE_URL+catalogueModel.getImage(), img, options);

        return view;
    }
}
