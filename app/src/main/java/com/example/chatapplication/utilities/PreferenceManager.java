package com.example.chatapplication.utilities;
import android.content.SharedPreferences;
import android.content.Context;
public class PreferenceManager {
     private static SharedPreferences sharedPreferences = null;
        public PreferenceManager(Context context){
            sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        public static void putBoolean(String key, boolean value){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
        public boolean getBoolean(String key){
            return sharedPreferences.getBoolean(key, false);
        }
        public static void putString(String key, String value){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
        public String getString(String key){
            return sharedPreferences.getString(key, null);
        }
        public void clearPreferences(){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
}
