package com.filutkie.cameracleaner.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.ui.CleanerActivity;

public class AutodeleteNotification {

    private Context context;

    public AutodeleteNotification(Context context) {
        this.context = context;
    }

    public void show(long sizeToDisplay) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preferences_name), Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(context.getString(
                R.string.pref_key_notification_show), true)) {
            Intent resultIntent = new Intent(context, CleanerActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(context, 0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification_brush)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(FileUtils.getHumanReadableByteCount(sizeToDisplay) + " " +
                            context.getString(R.string.action_cleaned_notification));

            if (!sharedPreferences.getBoolean(
                    context.getString(R.string.pref_key_notification_icon), true)) {
                mBuilder.setPriority(Notification.PRIORITY_MIN);
            }
            int mNotificationId = 328;
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }
}
