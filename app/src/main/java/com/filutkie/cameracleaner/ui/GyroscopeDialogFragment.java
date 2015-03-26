package com.filutkie.cameracleaner.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.filutkie.cameracleaner.R;

public class GyroscopeDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_gyro_not_found)
                .setMessage(R.string.dialog_message_gyro_not_found)
                .setPositiveButton(android.R.string.ok, this).create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int whichButton) {
        if (whichButton == Dialog.BUTTON_POSITIVE) {
        }
    }
}
