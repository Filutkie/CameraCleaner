package com.filutkie.cameracleaner.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.filutkie.cameracleaner.R;

public class CleanerActivity extends ActionBarActivity {

    public static final String TAG = CleanerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner);

        CleanerFragment fragment;
        if (savedInstanceState != null) {
            fragment = (CleanerFragment) getFragmentManager()
                    .findFragmentByTag(CleanerFragment.TAG);
        } else {
            fragment = new CleanerFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, CleanerFragment.TAG)
                    .commit();
        }
    }
}
