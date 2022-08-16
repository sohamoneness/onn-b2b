package com.b2bapp.onn.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedStorage {
    static SharedPreferences preference;

    private static String prefData = "com.lawpoint";

    public static String Id = "id";
    public static String AccessToken = "access_token";
    public static String Uesr_Fname = "fname";
    public static String User_Lname = "lname";
    public static String User_Email = "email";
    public static String User_Mobile = "mobile";
    public static String User_Customer_id = "customer_id";
    public static String User_Pin = "pin";

    public static void setValue(Context context, String key, String data) {
        preference = context.getSharedPreferences(prefData, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(key, data);
        editor.commit();
    }
    public static void setIntegerValue(Context context, String key, int data) {
        preference = context.getSharedPreferences(prefData, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt(key, data);
        editor.commit();
    }

    public static String getValue(Context context, String key) {
        preference = context.getSharedPreferences(prefData, Context.MODE_PRIVATE);
        String id = preference.getString(key, "");
        return id;
    }
    public static int getIntegerValue(Context context, String key) {
        preference = context.getSharedPreferences(prefData, Context.MODE_PRIVATE);
        int id = preference.getInt(key, 0);
        return id;
    }

    public static void resetValue(Context context) {
        preference = context.getSharedPreferences(prefData, Context.MODE_PRIVATE);
        preference.edit().clear().commit();
    }

}