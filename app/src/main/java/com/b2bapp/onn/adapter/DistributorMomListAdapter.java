package com.b2bapp.onn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b2bapp.onn.DistributorMomListActivity;
import com.b2bapp.onn.R;
import com.b2bapp.onn.model.DistributorMomModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DistributorMomListAdapter extends RecyclerView.Adapter<DistributorMomListAdapter.Hold> {
    Context context;
    List<DistributorMomModel> momList;
    final Calendar myCalendar= Calendar.getInstance();

    public DistributorMomListAdapter(DistributorMomListActivity distributorMomListActivity, List<DistributorMomModel> distMomList) {

        this.context = distributorMomListActivity;
        this.momList = distMomList;

    }

    @NonNull
    @Override
    public Hold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.distributor_mom_list_row, parent, false);
        return new Hold(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull Hold holder, int position) {

        DistributorMomModel dmm = momList.get(position);

        holder.tvDistName.setText(dmm.getDistributor_name());
        holder.tvNote.setText(dmm.getComment());

        String oldstring = dmm.getCreated_at();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oldstring);
            String newstring = new SimpleDateFormat("dd MMM, yyyy").format(date);
            holder.tvDate.setText(newstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return momList.size();
    }

    public class Hold extends RecyclerView.ViewHolder {

        TextView tvDistName, tvDate, tvNote;

        public Hold(@NonNull View itemView) {
            super(itemView);

            tvDistName = itemView.findViewById(R.id.tvDistName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNote = itemView.findViewById(R.id.tvNote);

        }
    }
}
