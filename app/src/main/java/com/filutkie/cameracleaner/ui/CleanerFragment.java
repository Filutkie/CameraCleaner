package com.filutkie.cameracleaner.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.filutkie.cameracleaner.CameraDirsSizeObtainerTask;
import com.filutkie.cameracleaner.CleanerApp;
import com.filutkie.cameracleaner.Consts;
import com.filutkie.cameracleaner.OnTaskCompleted;
import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.adapter.FolderArrayAdapter;
import com.filutkie.cameracleaner.model.Folder;
import com.filutkie.cameracleaner.utils.FileUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CleanerFragment extends Fragment implements OnTaskCompleted {

    static final String TAG = CleanerFragment.class.getSimpleName();

    private ImageButton cleanButton;
    private TextView cacheSizeTextView;
    private ListView foldersListView;
    private CameraDirsSizeObtainerTask task;
    private ArrayList<Folder> listItemsArray;

    public CleanerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cleaner_new, container, false);
        Log.d(TAG, "onCreateView start.");

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        initAnalytics();

        cacheSizeTextView = (TextView) rootView
                .findViewById(R.id.textview_cache_size_new);
        foldersListView = (ListView) rootView.findViewById(R.id.listview_folders_new);

        task = new CameraDirsSizeObtainerTask(this);
		task.execute();

        cleanButton = (ImageButton) rootView.findViewById(R.id.imagebutton_clean);
        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker t = ((CleanerApp) getActivity().getApplication()).getTracker(
                        CleanerApp.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Cleaner Button")
                        .setAction("Perform Clean")
                        .setLabel(cacheSizeTextView.getText().toString())
                        .build());
                File panoramaSessions = new File(FileUtils.PATH_CAMERA_PANORAMA_FOLDER);
                File tempSessions = new File(FileUtils.PATH_CAMERA_TEMP_FOLDER);

                FileUtils.deleteDir(tempSessions);
                FileUtils.deleteDir(panoramaSessions);

                cacheSizeTextView.setText("Cleaned!");
            }
        });
        Log.d(TAG, "onCreateView end.");

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_cleaner, menu);
    }

    @Override
    public void onTaskCompleted(HashMap<String, String> result) {
        Log.d(TAG, "onTaskCompleted started.");

        cacheSizeTextView.setText(result.get(Consts.FULL_SIZE));

        listItemsArray = new ArrayList<>();
        Folder folder = new Folder();
        folder.setName("panorama_sessions");
        folder.setSize(result.get(Consts.PANORAMA_SIZE));
        listItemsArray.add(folder);
        folder = new Folder();
        folder.setName("temp_sessions");
        folder.setSize(result.get(Consts.TEMP_SIZE));
        listItemsArray.add(folder);
        foldersListView.setAdapter(new FolderArrayAdapter(getActivity(),
                R.layout.list_item_folder, listItemsArray));
        Log.d(TAG, "onTaskCompleted ended.");
    }

    private void initAnalytics() {
        Tracker t = ((CleanerApp) getActivity().getApplication()).getTracker(
                CleanerApp.TrackerName.APP_TRACKER);
        t.setScreenName("CleanerFragment");
        t.send(new HitBuilders.AppViewBuilder().build());
    }
}
