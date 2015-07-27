package com.filutkie.cameracleaner.utils;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.filutkie.cameracleaner.R;

import java.io.File;
import java.util.Date;

/**
 * Common app and device specific utils.
 */
public class Utils {

    public static final String CAMERA_PACKAGE_NAME = "com.google.android.GoogleCamera";
    public static final String CAMERA_INTENT_FILTER_NAME = "android.hardware.action.NEW_PICTURE";
    public static final String CAMERA_PROCESSING_SERVICE_NAME = "com.android.camera.processing.ProcessingService";

    public static boolean hasGyroscope(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    }

    public static boolean isGoogleCameraPackageInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(CAMERA_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isCameraServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(CAMERA_PROCESSING_SERVICE_NAME)) {
                return true;
            }
        }
        return false;
    }

    public static void getGoogleCameraFromMarket(Context context) {
        Uri marketUri = Uri.parse("market://details?id=" + CAMERA_PACKAGE_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW, marketUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean isPano(Context context, Intent intent) {
        Cursor cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex("TITLE"));
        cursor.close();
        return title.startsWith("PANO");
    }

    public static String getPanoTitle(Context context, Intent intent) {
        Cursor cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex("TITLE"));
        cursor.close();
        return title;
    }

    public static boolean openFilemanager(Context context, String dir) {
        File file = new File(FileUtils.PATH_CAMERA_CACHE_FOLDER + "/" + dir);
        if (!file.exists()) {
            file = file.getParentFile();
        }
        try {
            // somehow this code works only with Solid Explorer file manager
            // I didn't find a suitable intent to open a specific folder in manager
            Uri selectedUri = Uri.parse(file.getAbsolutePath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("Camera Cleaner", "No suitable file manager has been found.");
            Toast.makeText(context, context.getString(R.string.action_err_open_filemanager),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // wip
    public static void getRelativeTime(long time) {
        Date from = new Date(time); // JANUARY_1_2007
        Date now = new Date(System.currentTimeMillis()); // APRIL_1_2007
        long diffInSeconds = (now.getTime() - from.getTime()) / 1000;
        long diff[] = new long[]{0, 0, 0, 0};
        /* sec */
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        /* min */
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        diff[0] = (diffInSeconds = (diffInSeconds / 24));

        String result = String.format(
                "%d day%s, %d hour%s, %d minute%s, %d second%s ago",
                diff[0],
                diff[0] > 1 ? "s" : "",
                diff[1],
                diff[1] > 1 ? "s" : "",
                diff[2],
                diff[2] > 1 ? "s" : "",
                diff[3],
                diff[3] > 1 ? "s" : "");
        Log.d("Utils", result);
    }

}
