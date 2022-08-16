package com.b2bapp.onn.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences {

	SharedPreferences prefs;
	public static String Id = "id";
	public static String User_fname = "fname";
	public static String User_lname = "lname";
	public static String User_Email = "email";
	public static String User_Mobile = "mobile";
	public static String User_Employee_id = "employee_id";
	public static String User_User_type = "user_type";
	public static String User_State = "state";
	public static String User_City = "city";

	public static String LAST_VISIT_ID = "last_visit_id";
	public static String LAST_SELECTED_AREA = "last_selected_area";

	
	public Preferences(Context context) {
		prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	public void storeStringPreference(Context context, String key, String value) {

		Editor e = prefs.edit();
		e.putString(key, value);
		e.commit();
	}

	public String getStringPreference(Context context, String key) {

		return prefs.getString(key, "");
	}

	public void storeIntPreference(Context context, String key, int value) {

		Editor e = prefs.edit();
		e.putInt(key, value);
		e.commit();
	}

	public int getIntPreference(Context context, String key) {

		return prefs.getInt(key, 0);
	}

	public void storeBooleanPreference(Context context, String key,
                                       Boolean value) {

		Editor e = prefs.edit();
		e.putBoolean(key, value);
		e.commit();
	}

	public Boolean getBooleanPreference(Context context, String key) {

		return prefs.getBoolean(key, false);
	}

	public void storeLongPreference(Context context, String key, long value) {

		Editor e = prefs.edit();
		e.putLong(key, value);
		e.commit();
	}

	public long getLongPreference(Context context, String key) {

		return prefs.getLong(key, 0);
	}

	public void clearAllPref (){

		prefs.edit().clear().commit();


	}


}
