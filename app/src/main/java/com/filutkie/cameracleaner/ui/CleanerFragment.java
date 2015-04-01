package com.filutkie.cameracleaner.ui;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.filutkie.cameracleaner.CleanerApp;
import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.adapter.FolderArrayAdapter;
import com.filutkie.cameracleaner.adapter.HistoryArrayAdapter;
import com.filutkie.cameracleaner.model.Folder;
import com.filutkie.cameracleaner.model.HistoryRecord;
import com.filutkie.cameracleaner.receiver.CameraReceiver;
import com.filutkie.cameracleaner.utils.FileUtils;
import com.filutkie.cameracleaner.utils.HistoryManager;
import com.filutkie.cameracleaner.utils.Utils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CleanerFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    public static final String TAG = CleanerFragment.class.getSimpleName();
    private static final String PREF_KEY_NOTIFICATION_SHOW = "notification_show";
    private static final String PREF_KEY_NOTIFICATION_ICON = "notification_icon";
    private static final String PREF_KEY_FIRST_LAUNCH = "first_launch";

    private Toolbar toolbar;
    private TextView sizeTextView;
    private ListView foldersListView;
    private ListView historyListView;
    private Switch autodeleteSwitch;
    private CheckBox notificationCheckBox;
    private CheckBox iconCheckBox;
    private UpdateReceiver updateReceiver;
    private SharedPreferences sharedPreferences;

    List<Folder> folderList;
    private long fullBytes = 0;

    public CleanerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cleaner, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        sizeTextView = (TextView) rootView.findViewById(R.id.textview_size_total);
        foldersListView = (ListView) rootView.findViewById(R.id.listview_folders_new);
        historyListView = (ListView) rootView.findViewById(R.id.listview_history);
        autodeleteSwitch = (Switch) rootView.findViewById(R.id.switch_autodelete);
        notificationCheckBox = (CheckBox) rootView.findViewById(R.id.checkbox_notification_show);
        iconCheckBox = (CheckBox) rootView.findViewById(R.id.checkbox_notification_icon);
        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_name), Context.MODE_PRIVATE);
        updateReceiver = new UpdateReceiver();

        autodeleteSwitch.setOnCheckedChangeListener(this);
        notificationCheckBox.setOnCheckedChangeListener(this);
        iconCheckBox.setOnCheckedChangeListener(this);

        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            int state = getBroadcastReceiverState();
            Log.d(TAG, "receiver status: " + getBroadcastReceiverStateString(state));
            if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
                autodeleteSwitch.setChecked(true);
            }
            if (!Utils.hasGyroscope(getActivity())) {
                GyroscopeDialogFragment gyroDialogFragment = new GyroscopeDialogFragment();
                gyroDialogFragment.show(getFragmentManager(), GyroscopeDialogFragment.TAG);
            }
            if (!Utils.isGoogleCameraPackageInstalled(getActivity())) {
                CameraDialogFragment cameraDialogFragment = new CameraDialogFragment();
                cameraDialogFragment.show(getFragmentManager(), CameraDialogFragment.TAG);
            }
            notificationCheckBox.setChecked(sharedPreferences.getBoolean(PREF_KEY_NOTIFICATION_SHOW, true));
            iconCheckBox.setChecked(sharedPreferences.getBoolean(PREF_KEY_NOTIFICATION_ICON, true));
        }
        folderList = FileUtils.getFoldersSize();
        fullBytes = FileUtils.getFullSize(folderList);
        HistoryManager historyManager = new HistoryManager(getActivity());
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (HistoryRecord record : historyManager.read()) {
            long millis = record.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
            Date resultdate = new Date(millis);
            stringArrayList.add(sdf.format(resultdate) + ", " +
                    FileUtils.getHumanReadableByteCount(record.getSize()));
        }
        foldersListView.setAdapter(new FolderArrayAdapter(getActivity(),
                R.layout.list_item_folder, folderList));
        historyListView.setAdapter(new HistoryArrayAdapter(getActivity(),
                R.layout.list_item_history, historyManager.read()));
        setListViewHeightBasedOnChildren(historyListView);
        if (fullBytes == 0) {
            sizeTextView.setText(getString(R.string.action_cleaned));
        } else {
            sizeTextView.setText(FileUtils.getHumanReadableByteCount(fullBytes));
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(PREF_KEY_FIRST_LAUNCH, true)) {
            autodeleteSwitch.setChecked(false);
            sharedPreferences.edit().putBoolean(PREF_KEY_FIRST_LAUNCH, false).apply();
        }
        getActivity().registerReceiver(updateReceiver,
                IntentFilter.create(Utils.CAMERA_INTENT_FILTER_NAME, "image/*"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(updateReceiver);
    }

    public void setCameraReceiverEnabled(boolean isEnabled) {
        int componentState = isEnabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        int currentState = getBroadcastReceiverState();
        if (componentState != currentState) {
            String state = isEnabled ? "registered" : "unregistered";
            Log.d(TAG, "Camera receiver " + state);
            PackageManager pm = getActivity().getPackageManager();
            ComponentName component = new ComponentName(getActivity(), CameraReceiver.class);
            pm.setComponentEnabledSetting(component, componentState, PackageManager.DONT_KILL_APP);
        }
    }

    private int getBroadcastReceiverState() {
        ComponentName component = new ComponentName(getActivity(), CameraReceiver.class);
        return getActivity().getPackageManager().getComponentEnabledSetting(component);
    }

    private String getBroadcastReceiverStateString(int status) {
        switch (status) {
            case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                return "enabled";
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
                return "disabled";
            case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
                return "enabled (default)";
            default:
                return "unknown, " + status;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_autodelete:
                setCameraReceiverEnabled(isChecked);
                break;
            case R.id.checkbox_notification_show:
                sharedPreferences.edit().putBoolean(PREF_KEY_NOTIFICATION_SHOW, isChecked).apply();
                break;
            case R.id.checkbox_notification_icon:
                sharedPreferences.edit().putBoolean(PREF_KEY_NOTIFICATION_ICON, isChecked).apply();
                break;
        }
    }

    /**
     * Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void initAnalytics() {
        Tracker t = ((CleanerApp) getActivity().getApplication()).getTracker();
        t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    /**
     * Receiver for updating TextViews and ListViews when app's screen is showing.
     * TODO update list
     */
    public class UpdateReceiver extends BroadcastReceiver {
        public UpdateReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (FileUtils.isPano(context, intent)) {
                folderList = FileUtils.getFoldersSize();
                fullBytes = FileUtils.getFullSize(folderList);
                sizeTextView.setText(FileUtils.getHumanReadableByteCount(fullBytes));
            }
        }
    }
}
