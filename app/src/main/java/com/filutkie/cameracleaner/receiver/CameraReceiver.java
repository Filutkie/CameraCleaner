package com.filutkie.cameracleaner.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.utils.FileUtils;
import com.filutkie.cameracleaner.utils.HistoryManager;
import com.filutkie.cameracleaner.utils.Utils;

import java.io.File;

/**
 * Receiver for autodelete feature. Checks if captured photo has PANO prefix
 * and deletes the cache.
 */
public class CameraReceiver extends BroadcastReceiver {

    private static final String TAG = CameraReceiver.class.getSimpleName();

    public CameraReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, "Broadcast received");

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preferences_name), Context.MODE_PRIVATE);

        if (FileUtils.isPano(context, intent)) {
            if (!Utils.isCameraServiceRunning(context)) {
                File panoramaSessions = new File(FileUtils.PATH_CAMERA_PANORAMA_FOLDER);
                File tempSessions = new File(FileUtils.PATH_CAMERA_TEMP_FOLDER);

                Log.d(TAG, "Calculating size...");
                long fullBytes = FileUtils.getFullSize(FileUtils.getFoldersSize());

                Log.d(TAG, "before delete");
                FileUtils.deleteDir(tempSessions);
                FileUtils.deleteDir(panoramaSessions);
                Log.d(TAG, "after delete");

                HistoryManager historyManager = new HistoryManager(context);
                historyManager.add(fullBytes);

                if (sharedPreferences.getBoolean("notification_show", true)) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_action_brush)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(FileUtils.getHumanReadableByteCount(fullBytes)
                                    + " cleaned");
                    if (!sharedPreferences.getBoolean("notification_icon", true)) {
                        mBuilder.setPriority(Notification.PRIORITY_MIN);
                    }
                    int mNotificationId = 001;
                    NotificationManager mNotifyMgr =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                }
            } else {
                Toast.makeText(context, "Postponing autodelete", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Photo is not PANO, skipping autodelete", Toast.LENGTH_SHORT).show();
        }
        Log.w(TAG, "Broadcast ended.");
    }
}
