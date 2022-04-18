package com.example.myapplication2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Dialog Fragment for module selection
 * https://developer.android.com/guide/fragments/dialogsZ
 */
public class ModuleDialogFragment extends DialogFragment {
    public static final String TAG = "ModuleDialog";
    OnMultiSelectListener onMultiSelectListener;
    OnSingleSelectListener onSingleSelectListener;
    ArrayList<String> moduleStringList;

    boolean[] selectedModule;
    ArrayList<Integer> moduleList = new ArrayList<>();

    // Used for multiple selections
    public ModuleDialogFragment(ArrayList<String> moduleStringList, OnMultiSelectListener onMultiSelectListener){
        this.onMultiSelectListener = onMultiSelectListener;
        this.moduleStringList = moduleStringList;
    }

    // Used for single selection
    public ModuleDialogFragment(ArrayList<String> moduleStringList, OnSingleSelectListener onSingleSelectListener) {
        this.onSingleSelectListener = onSingleSelectListener;
        this.moduleStringList = moduleStringList;
    }

    public interface OnSingleSelectListener {
        void onResult(Integer i);
    }

    public interface OnMultiSelectListener {
        void onResult(ArrayList<Integer> integerArrayList);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setMessage("No modules available!")
                .create();

        String[] moduleArray = moduleStringList.toArray(new String[moduleStringList.size()]);

        if (moduleArray.length == 0) {
            return dialog;
        }

        if (onSingleSelectListener != null) {
            return new AlertDialog.Builder(requireContext())
                    .setTitle("Select Module")
                    .setItems(moduleArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onSingleSelectListener.onResult(i);
                            Log.i(TAG, "onClick: module " + moduleStringList.get(i) + " chosen.");
                        }
                    })
                    .create();
        }

        if (onMultiSelectListener != null) {

            selectedModule = new boolean[moduleArray.length];

            return new AlertDialog.Builder(requireContext())
                    .setTitle("Select Modules")
                    .setCancelable(false)
                    .setMultiChoiceItems(moduleArray, selectedModule, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            if (b) {
                                moduleList.add(i);
                                Collections.sort(moduleList);
                                Log.i(TAG, "onClick: module " + moduleStringList.get(i) + " chosen.");
                            } else {
                                moduleList.remove(Integer.valueOf(i));
                            }
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onMultiSelectListener.onResult(moduleList);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Arrays.fill(selectedModule, false);
                            moduleList.clear();
                        }
                    })
                    .create();
        }
        return dialog;
    }
}