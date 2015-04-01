package com.filutkie.cameracleaner.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.filutkie.cameracleaner.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class HistoryManager {

    private static final String TAG = HistoryManager.class.getSimpleName();

    private SharedPreferences sharedPreferences;

    public HistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preferences_name), Context.MODE_PRIVATE);
    }

    public void read() {

    }

    public void add(long size) {
        String history = sharedPreferences.getString("history", "");
        Calendar calendar = GregorianCalendar.getInstance();
        String record = size + "," + calendar.getTimeInMillis() + ";";
        sharedPreferences.edit().putString("history", history.concat(record)).apply();

        Log.d(TAG, "History: " + history);
    }

}
