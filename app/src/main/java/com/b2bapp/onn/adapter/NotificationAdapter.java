package com.b2bapp.onn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b2bapp.onn.NotificationActivity;
import com.b2bapp.onn.R;
import com.b2bapp.onn.model.NotificationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Hold> {

    Context context;
    List<NotificationModel> nList;

    public NotificationAdapter(NotificationActivity notificationActivity, List<NotificationModel> notiList) {

        this.context = notificationActivity;
        this.nList = notiList;

    }

    @NonNull
    @Override
    public NotificationAdapter.Hold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
        return new Hold(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.Hold holder, int position) {

        NotificationModel nm = nList.get(position);

        holder.tvNotiBody.setText(nm.getBody());
        holder.tvNotiHeader.setText(nm.getTitle());

        String oldstring = nm.getCreated_at();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oldstring);
            String newstring = new SimpleDateFormat("dd MMM, yyyy").format(date);
            holder.tvDate.setText(newstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!nm.getRead_flag().equals("1")){
            holder.redDotLL.setVisibility(View.VISIBLE);
        }else{
            holder.redDotLL.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return nList.size();
    }

    public class Hold extends RecyclerView.ViewHolder {

        TextView tvNotiHeader, tvNotiBody, tvDate;
        LinearLayout redDotLL;

        public Hold(@NonNull View itemView) {
            super(itemView);

            tvNotiHeader = itemView.findViewById(R.id.tvNotiHeader);
            tvNotiBody = itemView.findViewById(R.id.tvNotiBody);
            tvDate = itemView.findViewById(R.id.tvDate);
            redDotLL = itemView.findViewById(R.id.redDotLL);

        }
    }
}
