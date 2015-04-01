package com.filutkie.cameracleaner;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class CleanerApp extends Application {

    public CleanerApp() {
        super();
    }

    public synchronized Tracker getTracker() {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        return analytics.newTracker(R.xml.app_tracker);
    }
}
