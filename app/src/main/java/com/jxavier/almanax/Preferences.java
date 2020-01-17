package com.jxavier.almanax;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

public class Preferences {
    public static void setPrefs(String key, String value, Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setPrefs(String key, boolean value, Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setPrefs(String key, Integer value, Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getPrefs(String key, Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, "notfound");
    }

    public static Integer getPrefsInt(String key, Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(key, -1);
    }


    public static boolean getPrefsBoolean(String key, Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(key, false);
    }

    public static void setArrayPrefs(String arrayName, ArrayList<String> array, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++)
            editor.putString(arrayName + "_" + i, array.get(i));
        editor.apply();
    }

    public static ArrayList<String> getArrayPrefs(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for(int i=0;i<size;i++)
            array.add(prefs.getString(arrayName + "_" + i, null));
        return array;
    }
}
