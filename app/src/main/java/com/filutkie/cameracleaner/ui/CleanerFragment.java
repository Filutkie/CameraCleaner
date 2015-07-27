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
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.filutkie.cameracleaner.CleanerApp;
import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.adapter.FolderArrayAdapter;
import com.filutkie.cameracleaner.adapter.HistoryArrayAdapter;
import com.filutkie.cameracleaner.io.HistoryManager;
import com.filutkie.cameracleaner.model.Folder;
import com.filutkie.cameracleaner.receiver.CameraReceiver;
import com.filutkie.cameracleaner.utils.FileUtils;
import com.filutkie.cameracleaner.utils.Utils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.util.List;

public class CleanerFragment extends Fragment implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener {

    public static final String TAG = "CleanerFragment";
    private static final String PREF_KEY_NOTIFICATION_SHOW = "notification_show";
    private static final String PREF_KEY_NOTIFICATION_ICON = "notification_icon";
    private static final String PREF_KEY_FIRST_LAUNCH = "first_launch";

    private Button cleanButton;
    private TextView sizeTextView;
    private TextView historyHintTextView;
    private ListView foldersListView;
    private ListView historyListView;
    private SwitchCompat autodeleteSwitch;
    private CheckBox notificationCheckBox;
    private CheckBox iconCheckBox;
    private UpdateReceiver updateReceiver;
    private SharedPreferences sharedPreferences;
    private FolderArrayAdapter folderArrayAdapter;
    private Tracker tracker;

    private List<Folder> folderList;
    private long fullSizeInBytes = 0;

    public CleanerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cleaner, container, false);

        tracker = CleanerApp.getTracker();
        cleanButton = (Button) rootView.findViewById(R.id.button_clean);
        sizeTextView = (TextView) rootView.findViewById(R.id.textview_size_total);
        historyHintTextView = (TextView) rootView.findViewById(R.id.textview_history_hint);
        foldersListView = (ListView) rootView.findViewById(R.id.listview_folders_new);
        historyListView = (ListView) rootView.findViewById(R.id.listview_history);
        autodeleteSwitch = (SwitchCompat) rootView.findViewById(R.id.switch_autodelete);
        notificationCheckBox = (CheckBox) rootView.findViewById(R.id.checkbox_notification_show);
        iconCheckBox = (CheckBox) rootView.findViewById(R.id.checkbox_notification_icon);
        updateReceiver = new UpdateReceiver();
        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_name), Context.MODE_PRIVATE);

        cleanButton.setOnClickListener(this);
        foldersListView.setOnItemClickListener(this);
        autodeleteSwitch.setOnCheckedChangeListener(this);
        notificationCheckBox.setOnCheckedChangeListener(this);
        iconCheckBox.setOnCheckedChangeListener(this);

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
        folderList = FileUtils.getFoldersList();
        fullSizeInBytes = FileUtils.getListFullSizeInBytes(folderList);
        HistoryManager historyManager = new HistoryManager(getActivity());

        folderArrayAdapter = new FolderArrayAdapter(getActivity(),
                R.layout.list_item_folder, folderList);

        HistoryArrayAdapter historyArrayAdapter = new HistoryArrayAdapter(getActivity(),
                R.layout.list_item_history, historyManager.read());
        foldersListView.setAdapter(folderArrayAdapter);

        if (historyArrayAdapter.isEmpty()) {
            historyHintTextView.setVisibility(View.VISIBLE);
        } else {
            historyHintTextView.setVisibility(View.GONE);
            historyListView.setAdapter(historyArrayAdapter);
        }
        setListViewHeightBasedOnChildren(historyListView);

        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.AppViewBuilder().build());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // the autodelete switch is enabled by default
        // but we are making it disabled for the first time app is launched
        if (sharedPreferences.getBoolean(PREF_KEY_FIRST_LAUNCH, true)) {
            autodeleteSwitch.setChecked(false);
            sharedPreferences.edit().putBoolean(PREF_KEY_FIRST_LAUNCH, false).apply();
        }
        if (fullSizeInBytes == 0) {
            sizeTextView.setText(getString(R.string.action_cleaned));
        } else {
            sizeTextView.setText(FileUtils.getHumanReadableByteCount(fullSizeInBytes));
        }
        getActivity().registerReceiver(updateReceiver,
                IntentFilter.create(Utils.CAMERA_INTENT_FILTER_NAME, "image/*"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(updateReceiver);
    }

    /**
     * Sets enabled or disabled camera receiver responsible for autodelete.
     *
     * @see CameraReceiver
     */
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
            sendEvent(
                    TAG,
                    "autodelete_switch",
                    "receiver " + state);
        }
    }

    /**
     * Checks whether camera receiver enabled or disabled.
     *
     * @return integer value constant.
     * @see PackageManager
     */
    private int getBroadcastReceiverState() {
        ComponentName component = new ComponentName(getActivity(), CameraReceiver.class);
        return getActivity().getPackageManager().getComponentEnabledSetting(component);
    }

    /**
     * Just for logging purposes.
     */
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
    private static void setListViewHeightBasedOnChildren(ListView listView) {
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

    /*
     * Open the file manager when folder item clicked.
     * Or do nothing if the file manager not found.
     * Works well only with Solid Explorer.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Folder folderItem = ((Folder) parent.getAdapter().getItem(position));
        boolean isSuccessful = Utils.openFilemanager(getActivity(), folderItem.getName());
        String success = isSuccessful ? "opened": "error";
        sendEvent(
                TAG,
                "folders_list_click",
                folderItem.getName() + ", " + success);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_clean) {
            if (!Utils.isCameraServiceRunning(getActivity())) {
                FileUtils.deleteDir(new File(FileUtils.PATH_CAMERA_PANORAMA_FOLDER));
                FileUtils.deleteDir(new File(FileUtils.PATH_CAMERA_TEMP_FOLDER));
                sizeTextView.setText(getString(R.string.action_cleaned));
                sendEvent(
                        TAG,
                        "button_clean",
                        FileUtils.getHumanReadableByteCount(fullSizeInBytes));
            } else {
                sendEvent(
                        TAG,
                        "button_clean",
                        "processing not finished");
                Toast.makeText(
                        getActivity(),
                        getString(R.string.toast_please_wait),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void sendEvent(String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    /**
     * Receiver for updating TextViews and ListViews when app's screen is showing.
     */
    public class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isPano(context, intent)) {
                folderList = FileUtils.getFoldersList();
                fullSizeInBytes = FileUtils.getListFullSizeInBytes(folderList);
                folderArrayAdapter.clear();
                folderArrayAdapter.addAll(folderList);
                sizeTextView.setText(FileUtils.getHumanReadableByteCount(fullSizeInBytes));
                sendEvent(
                        TAG,
                        "Update Receiver",
                        "UpdateReceiver.onReceive()");
            }
        }
    }
}
