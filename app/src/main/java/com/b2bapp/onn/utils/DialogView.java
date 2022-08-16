package com.b2bapp.onn.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.b2bapp.onn.R;


public class DialogView {

    static ProgressDialog mAlertDialog;

    public static Dialog customSpinProgress, customNoNetwork;

    public DialogView() {}

    public void showSingleButtonDialog(Context context, String header, String msg) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.inflate_custom_alert_dialog, null);

        final AlertDialog alertD = new AlertDialog.Builder(context).create();
        TextView tvHeader=(TextView)promptView.findViewById(R.id.tvHeader);
        tvHeader.setText(header);
        TextView tvMsg=(TextView)promptView.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        Button btnCancel = (Button) promptView.findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.GONE);
        btnCancel.setText("Cancel");
        Button btnOk = (Button) promptView.findViewById(R.id.btnOk);
        btnOk.setBackgroundColor(context.getResources().getColor(R.color.welcome));
        btnOk.setText("Ok");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertD.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertD.dismiss();

            }
        });

        alertD.setView(promptView);
        try {
            alertD.show();
        }
        catch (WindowManager.BadTokenException e) {
            //use a log message
        }

    }

    public void showSpinProgress(Context context, String msg) {
        mAlertDialog = new ProgressDialog(context);
        mAlertDialog.setMessage(msg);
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mAlertDialog.show();

    }

    public void showCustomSpinProgress(Context context) {

        customSpinProgress = new Dialog(context);
        customSpinProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customSpinProgress.setContentView(R.layout.dialog_loading);
        customSpinProgress.setCanceledOnTouchOutside(false);
        customSpinProgress.setCancelable(true);
        customSpinProgress.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        WindowManager.LayoutParams wlmp = customSpinProgress.getWindow()
                .getAttributes();
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.height = WindowManager.LayoutParams.MATCH_PARENT;
        customSpinProgress.show();
    }

    public void dismissCustomSpinProgress() {
        if (customSpinProgress != null)
            customSpinProgress.dismiss();
    }

    public void showHorizontalProgress(Context context, String msg) {
        mAlertDialog = new ProgressDialog(context);
        mAlertDialog.setMessage(msg);
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setIndeterminate(false);
        mAlertDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mAlertDialog.setProgress(0);
        mAlertDialog.setMax(100);
        mAlertDialog.show();
    }

    public void dismissProgress() {
        if (mAlertDialog.isShowing())
            mAlertDialog.dismiss();
    }


}

