package com.b2bapp.onn.utils;

import android.content.Context;
import android.widget.Toast;

public class CSnackBar {
    public static void show(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
