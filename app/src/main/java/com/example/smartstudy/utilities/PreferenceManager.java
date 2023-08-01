package com.example.smartstudy.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Set;

public class PreferenceManager {

    private final SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void clearPreferences() {
        editor.clear();
        editor.apply();
    }

}
