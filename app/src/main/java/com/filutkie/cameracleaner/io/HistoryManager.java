package com.filutkie.cameracleaner.io;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.model.HistoryRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class HistoryManager {

    private static final String TAG = HistoryManager.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private String PREF_KEY_HISTORY = "history";

    public HistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preferences_name), Context.MODE_PRIVATE);
    }

    public List<HistoryRecord> read() {
        String history = sharedPreferences.getString(PREF_KEY_HISTORY, "");
        if (history != null && !history.isEmpty()) {
            List<HistoryRecord> historyRecords = new ArrayList<>();
            HistoryRecord historyRecord;
            String[] records = history.split(";");
            for (String record : records) {
                String[] items = record.split(",");
                historyRecord = new HistoryRecord(Long.parseLong(items[0]), Long.parseLong(items[1]));
                historyRecords.add(historyRecord);
            }
            Collections.reverse(historyRecords);
            return historyRecords.size() > 5 ? historyRecords.subList(0, 5) : historyRecords;
        }
        return new ArrayList<>();
    }

    public void add(long size) {
        String history = sharedPreferences.getString(PREF_KEY_HISTORY, "");
        Calendar calendar = GregorianCalendar.getInstance();
        String record = size + "," + calendar.getTimeInMillis() + ";";
        if (history != null) {
            sharedPreferences.edit().putString(PREF_KEY_HISTORY, history.concat(record)).apply();
        }
        Log.d(TAG, "History: " + history);
    }

}
