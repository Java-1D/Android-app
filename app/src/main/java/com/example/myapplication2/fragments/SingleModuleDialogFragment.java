package com.example.myapplication2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

// https://developer.android.com/guide/fragments/dialogs
public class SingleModuleDialogFragment extends DialogFragment {
    public static String TAG = "SingleModuleDialog";

    static SingleModuleDialogFragment newInstance(int num) {
        SingleModuleDialogFragment singleModuleDialogFragment = new SingleModuleDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        singleModuleDialogFragment.setArguments(bundle);

        return singleModuleDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage("Select Module")
                .setPositiveButton("OK", (dialog, which) -> {

                } )
                .create();
    }

}
