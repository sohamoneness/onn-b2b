package com.b2bapp.onn.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b2bapp.onn.ProductListActivity;
import com.b2bapp.onn.R;
import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.model.CategoryModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class RvCategoryAdapter extends RecyclerView.Adapter<RvCategoryAdapter.servicesListHolder> implements View.OnClickListener {

    private final String[] serviceName;
    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;
    private ArrayList<CategoryModel> categoryModelArrayList;
    private final int limit = 10;
    Activity context;
    public RvCategoryAdapter(Activity mContext, ArrayList<CategoryModel> categoryModelArrayList, String[] serviceName) {
        this.categoryModelArrayList = categoryModelArrayList;
        this.serviceName = serviceName;
        this.context = mContext;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.default_image)
                //.showImageForEmptyUri(R.drawable.default_image)
                // .showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public RvCategoryAdapter.servicesListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_rv_category, parent, false);
        return new RvCategoryAdapter.servicesListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull servicesListHolder holder, int position) {
        final CategoryModel categoryModel=categoryModelArrayList.get(position);

        holder.tvTitle.setText(categoryModel.getName());
        holder.liCategory.setTag(position);
        holder.liCategory.setOnClickListener(this);

        //imageLoader.displayImage(WebServices.categoryImageURL+categoryModel.getImage(), holder.imgCategory, options);
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        switch (view.getId()){
            case R.id.liCategory:
                //GlobalVariable.category_id = categoryModelArrayList.get(position).getId();
                //ProductListActivity.callProductList();
                ((ProductListActivity) context).getProductsByCategoryTask(categoryModelArrayList.get(position).getId());
                break;
        }
    }

    public class servicesListHolder extends RecyclerView.ViewHolder {
        //private final TextView tvServiceName;
        //public ImageView imgCategory;
        public TextView tvTitle;
        public LinearLayout liCategory;

        public servicesListHolder(View view) {
            super(view);
            //imgCategory = (ImageView) view.findViewById(R.id.imgCategory);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            liCategory = (LinearLayout) view.findViewById(R.id.liCategory);
        }
    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }
}
