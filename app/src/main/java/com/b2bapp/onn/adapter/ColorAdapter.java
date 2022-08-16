package com.b2bapp.onn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2bapp.onn.R;
import com.b2bapp.onn.model.ColorModel;

import java.util.ArrayList;

public class ColorAdapter extends BaseAdapter {
    ArrayList<ColorModel> cityValArrayList = new ArrayList<ColorModel>();
    Context context;
    LayoutInflater inflater;

    public ColorAdapter(ArrayList<ColorModel> cities, Context context ) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.cityValArrayList=cities;
    }
    @Override
    public int getCount() {
        return cityValArrayList.size();
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
        Holder holder =null;
        ColorModel colorModel=cityValArrayList.get(position);

        if (convertView == null){
            inflater = getLayoutInflater();
            holder=new Holder();
            convertView = inflater.inflate(R.layout.inflate_color, parent, false);
            holder.tvSpinType = (TextView) convertView.findViewById(R.id.tvSpin);

            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
        }

        holder.tvSpinType.setText(colorModel.getName());

        return convertView;
    }

    private LayoutInflater getLayoutInflater(){
        return inflater;
    }

    class Holder{

        TextView tvSpinType;
    }
}
