package com.example.myapplication2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication2.interfaces.DialogInterfaces.CustomDialogInterface;
import com.example.myapplication2.interfaces.DialogInterfaces.IntegerListDialogInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// https://developer.android.com/guide/fragments/dialogs
public class ModuleDialogFragment extends DialogFragment {
    final public static String TAG = "ModuleDialog";
    IntegerListDialogInterface integerListDialogInterface;
    CustomDialogInterface customDialogInterface;
    ArrayList<String> moduleStringList;
    boolean[] selectedModule;
    ArrayList<Integer> moduleList = new ArrayList<>();


    public ModuleDialogFragment(IntegerListDialogInterface integerListDialogInterface, ArrayList<String> moduleStringList){
        this.integerListDialogInterface = integerListDialogInterface;
        this.moduleStringList = moduleStringList;
    }

    public ModuleDialogFragment(CustomDialogInterface customDialogInterface, ArrayList<String> moduleStringList) {
        this.customDialogInterface = customDialogInterface;
        this.moduleStringList = moduleStringList;
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

        if (customDialogInterface != null) {
            return new AlertDialog.Builder(requireContext())
                    .setTitle("Select Module")
                    .setItems(moduleArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            customDialogInterface.onResult(i);
                            Log.i(TAG, "onClick: module " + moduleStringList.get(i) + " chosen.");
                        }
                    })
                    .create();
        }

        if (integerListDialogInterface != null) {

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
                            integerListDialogInterface.onResult(moduleList);
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








