package com.filutkie.cameracleaner;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class CleanerApp extends Application {
    private final String TRACKER_ID = "UA-51086168-2";

    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public static synchronized Tracker getTracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        analytics.setDryRun(false);

        tracker = analytics.newTracker(TRACKER_ID);
        tracker.enableExceptionReporting(true);
    }
}
