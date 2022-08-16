package com.b2bapp.onn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.AreaModel;
import com.b2bapp.onn.model.DistributorModel;

import java.util.ArrayList;

public class DistributorSpinAdapter extends BaseAdapter {
    ArrayList<DistributorModel> areaModelArrayList = new ArrayList<DistributorModel>();
    Context context;
    LayoutInflater inflater;

    public DistributorSpinAdapter(ArrayList<DistributorModel> areaModelArrayList, Context context ) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.areaModelArrayList=areaModelArrayList;
    }
    @Override
    public int getCount() {
        return areaModelArrayList.size();
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
        DistributorSpinAdapter.Holder holder =null;
        DistributorModel areaModel=areaModelArrayList.get(position);

        if (convertView == null){
            inflater = getLayoutInflater();
            holder=new DistributorSpinAdapter.Holder();
            convertView = inflater.inflate(R.layout.inflate_area_spin, parent, false);
            holder.tvSpinType = (TextView) convertView.findViewById(R.id.tvSpin);

            convertView.setTag(holder);
        }else{
            holder=(DistributorSpinAdapter.Holder)convertView.getTag();
        }

        holder.tvSpinType.setText(areaModel.getBussiness_name());

        return convertView;
    }

    private LayoutInflater getLayoutInflater(){
        return inflater;
    }

    class Holder{

        TextView tvSpinType;
    }
}

