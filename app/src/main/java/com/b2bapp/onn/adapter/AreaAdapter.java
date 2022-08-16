package com.b2bapp.onn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.AreaModel;
import com.b2bapp.onn.model.ColorModel;

import java.util.ArrayList;

public class AreaAdapter extends BaseAdapter {
    ArrayList<AreaModel> areaModelArrayList = new ArrayList<AreaModel>();
    Context context;
    LayoutInflater inflater;

    public AreaAdapter(ArrayList<AreaModel> areaModelArrayList, Context context ) {

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
        AreaAdapter.Holder holder =null;
        AreaModel areaModel=areaModelArrayList.get(position);

        if (convertView == null){
            inflater = getLayoutInflater();
            holder=new AreaAdapter.Holder();
            convertView = inflater.inflate(R.layout.inflate_area_spin, parent, false);
            holder.tvSpinType = (TextView) convertView.findViewById(R.id.tvSpin);

            convertView.setTag(holder);
        }else{
            holder=(AreaAdapter.Holder)convertView.getTag();
        }

        holder.tvSpinType.setText(areaModel.getName());

        return convertView;
    }

    private LayoutInflater getLayoutInflater(){
        return inflater;
    }

    class Holder{

        TextView tvSpinType;
    }
}

