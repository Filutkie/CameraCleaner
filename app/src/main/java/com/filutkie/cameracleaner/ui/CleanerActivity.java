package com.filutkie.cameracleaner.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.filutkie.cameracleaner.utils.FileUtils;
import com.filutkie.cameracleaner.R;

public class CleanerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner);

        Log.v("tag", "Activity onCreate started().");
        CleanerFragment fragment;
        if (savedInstanceState != null) {
            fragment = (CleanerFragment) getFragmentManager()
                    .findFragmentByTag(CleanerFragment.TAG);
            Log.w("tag", "Fragment found by tag.");
        } else {
            fragment = new CleanerFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, CleanerFragment.TAG)
                    .commit();
            Log.w("tag", "Fragment created from scratch.");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_cleaner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_browse:
                try {
                    Uri selectedUri = Uri.parse(FileUtils.PATH_CAMERA_CACHE_FOLDER);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(selectedUri, "resource/folder");
                    startActivity(intent);
                } catch (ActivityNotFoundException exception) {
                    Toast.makeText(this, "Can't open file manager.", Toast.LENGTH_SHORT).show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
