package com.example.myapplication2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication2.CreateEventActivity;
import com.example.myapplication2.interfaces.DialogInterfaces.IntegerDialogInterface;
import com.example.myapplication2.interfaces.DialogInterfaces.IntegerListDialogInterface;
import com.example.myapplication2.interfaces.DialogInterfaces.URIDialogInterface;

import java.util.ArrayList;
import java.util.Arrays;

// https://developer.android.com/guide/fragments/dialogs
public class ModuleDialogFragment extends DialogFragment {
    final public static String TAG = "ModuleDialog";
    IntegerListDialogInterface integerListDialogInterface;
    IntegerDialogInterface integerDialogInterface;
    ArrayList<String> moduleStringList;


    public ModuleDialogFragment(IntegerListDialogInterface integerListDialogInterface, ArrayList<String> moduleStringList){
        this.integerListDialogInterface = integerListDialogInterface;
        this.moduleStringList = moduleStringList;
    }

    public ModuleDialogFragment(IntegerDialogInterface integerDialogInterface, ArrayList<String> moduleStringList) {
        this.integerDialogInterface = integerDialogInterface;
        this.moduleStringList = moduleStringList;
    }

    public void setModuleStringList(ArrayList<String> moduleStringList) {
        this.moduleStringList = moduleStringList;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        try {
            String[] moduleArray = moduleStringList.toArray(new String[moduleStringList.size()]);

            if (integerDialogInterface != null) {
                return new AlertDialog.Builder(requireContext())
                        .setTitle("Select Module")
                        .setItems(moduleArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                integerDialogInterface.onResult(i);
                            }
                        })
                        .create();
            }

            if (integerListDialogInterface != null) {
                return new AlertDialog.Builder(requireContext())
                        .setTitle("Select Module")
                        .create();
            }

        } catch (Exception e) {
            return new AlertDialog.Builder(requireContext())
                    .setMessage("No modules avaliable!")
                    .create();
        }

        return new AlertDialog.Builder(requireContext())
                .setMessage("No modules avaliable!")
                .create();
    }
}








