package com.example.myapplication2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class YesNoDialogFragment extends DialogFragment {
    public static String TAG = "YesNoDialogFragment";

    String message;
    OnClickListener onClickListener;

    public YesNoDialogFragment(String message, OnClickListener onClickListener) {
        this.message = message;
        this.onClickListener = onClickListener;
    }

    public static interface OnClickListener{
        void onResult(boolean bool);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onClickListener.onResult(true);
                        Log.i(TAG, "Selected 'Yes'");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onClickListener.onResult(false);
                        Log.i(TAG, "Selected 'No'");
                    }
                })
                .create();
    }
}