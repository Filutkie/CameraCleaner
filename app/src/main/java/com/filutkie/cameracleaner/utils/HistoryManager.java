package com.filutkie.cameracleaner.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.model.HistoryRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HistoryManager {

    private static final String TAG = HistoryManager.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private String PREF_HISTORY = "history";

    public HistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preferences_name), Context.MODE_PRIVATE);
    }

    public List<HistoryRecord> read() {
        String history = sharedPreferences.getString(PREF_HISTORY, "");
        if (!history.isEmpty()) {
            List<HistoryRecord> historyRecords = new ArrayList<>();
            HistoryRecord historyRecord;
            String[] records = history.split(";");
            for (String record : records) {
                String[] items = record.split(",");
                historyRecord = new HistoryRecord(Long.parseLong(items[0]), Long.parseLong(items[1]));
                historyRecords.add(historyRecord);
            }

            return historyRecords;
        }
        return null;
    }

    public void add(long size) {
        String history = sharedPreferences.getString(PREF_HISTORY, "");
        Calendar calendar = GregorianCalendar.getInstance();
        String record = size + "," + calendar.getTimeInMillis() + ";";
        sharedPreferences.edit().putString(PREF_HISTORY, history.concat(record)).apply();

        Log.d(TAG, "History: " + history);
    }

}
