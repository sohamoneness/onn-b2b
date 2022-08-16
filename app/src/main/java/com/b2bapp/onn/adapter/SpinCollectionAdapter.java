package com.b2bapp.onn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.CollectionModel;
import com.b2bapp.onn.model.DistributorModel;

import java.util.ArrayList;

public class SpinCollectionAdapter extends BaseAdapter {
    ArrayList<CollectionModel> collectionModelArrayList = new ArrayList<CollectionModel>();
    Context context;
    LayoutInflater inflater;

    public SpinCollectionAdapter(ArrayList<CollectionModel> collectionModelArrayList, Context context ) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.collectionModelArrayList=collectionModelArrayList;
    }
    @Override
    public int getCount() {
        return collectionModelArrayList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        SpinCollectionAdapter.Holder holder =null;
        CollectionModel collectionModel=collectionModelArrayList.get(position);

        if (convertView == null){
            inflater = getLayoutInflater();
            holder=new SpinCollectionAdapter.Holder();
            convertView = inflater.inflate(R.layout.inflate_area_spin, parent, false);
            holder.tvSpinType = (TextView) convertView.findViewById(R.id.tvSpin);

            convertView.setTag(holder);
        }else{
            holder=(SpinCollectionAdapter.Holder)convertView.getTag();
        }

        holder.tvSpinType.setText(collectionModel.getName());

        return convertView;
    }

    private LayoutInflater getLayoutInflater(){
        return inflater;
    }

    class Holder{

        TextView tvSpinType;
    }
}