package com.filutkie.cameracleaner.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.utils.Utils;

public class CameraDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String TAG = CameraDialogFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_camera_not_installed)
                .setMessage(R.string.dialog_message_camera_not_installed)
                .setPositiveButton(R.string.action_get_on_google_play, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int whichButton) {
        try {
            Utils.getGoogleCameraFromMarket(getActivity());
        } catch (ActivityNotFoundException e) {
            // open browser url if Google Play is not installed
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +
                            Utils.CAMERA_PACKAGE_NAME)));
        }

    }

}
