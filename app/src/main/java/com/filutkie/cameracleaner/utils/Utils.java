package com.filutkie.cameracleaner.utils;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.filutkie.cameracleaner.R;

import java.io.File;

/**
 * Common app and device specific utils.
 */
public class Utils {

    public static final String CAMERA_PACKAGE_NAME = "com.google.android.GoogleCamera";
    public static final String CAMERA_INTENT_FILTER_NAME = "android.hardware.action.NEW_PICTURE";
    public static final String CAMERA_PROCESSING_SERVICE_NAME =
            "com.android.camera.processing.ProcessingService";

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

    public static void openFilemanager(Context context, File dir) {
        try {
            // somehow this code works only with Solid Explorer file manager
            // I didn't find a suitable intent to open a specific folder in manager
            Uri selectedUri = Uri.parse(FileUtils.PATH_CAMERA_CACHE_FOLDER);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.action_err_open_filemanager), Toast.LENGTH_SHORT).show();
        }
    }

}
