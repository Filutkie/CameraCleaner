package com.filutkie.cameracleaner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.filutkie.cameracleaner.CleanerApp;
import com.filutkie.cameracleaner.io.HistoryManager;
import com.filutkie.cameracleaner.utils.AutodeleteNotification;
import com.filutkie.cameracleaner.utils.FileUtils;
import com.filutkie.cameracleaner.utils.Utils;
import com.google.android.gms.analytics.HitBuilders;

import java.io.File;

/**
 * Receiver used for autodelete feature.
 * The onReceive method is triggered whenever an user makes a photo with Google Camera.
 */
public class CameraReceiver extends BroadcastReceiver {

    private static final String TAG = CameraReceiver.class.getSimpleName();

    public CameraReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = Utils.getPanoTitle(context, intent);

        // the title contains something like "PANO_20150423_021551".
        if (title.startsWith("PANO")) {
            String datetime = title.split("_", 2)[1];

            File panoSessionsDir = FileUtils.getCorrespondingDir(
                    FileUtils.PATH_CAMERA_PANORAMA_FOLDER, datetime);
            File tempSessionsDir = FileUtils.getCorrespondingDir(
                    FileUtils.PATH_CAMERA_TEMP_FOLDER, datetime);

            long fullSizeInBytes =
                    FileUtils.getDirSize(panoSessionsDir) + FileUtils.getDirSize(tempSessionsDir);

            CleanerApp.getTracker().send(
                    new HitBuilders.EventBuilder()
                            .setCategory(TAG)
                            .setAction("onReceive")
                            .setLabel(FileUtils.getHumanReadableByteCount(fullSizeInBytes))
                            .build()
            );

            FileUtils.deleteDir(panoSessionsDir);
            FileUtils.deleteDir(tempSessionsDir);

            HistoryManager historyManager = new HistoryManager(context);
            historyManager.add(fullSizeInBytes);

            AutodeleteNotification notification = new AutodeleteNotification(context);
            notification.show(fullSizeInBytes);
        } else {
            Log.d(TAG, "Photo is not PANO");
        }
    }

}
