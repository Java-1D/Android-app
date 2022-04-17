package com.example.myapplication2.fragments;

import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Time Picker Fragment
 * https://developer.android.com/guide/topics/ui/controls/pickers
 */
public class TimePickerDialogFragment extends DialogFragment {
    public static final String TAG = "TimePickerDialog";
    OnTimeSetListener timeDialogInterface;
    Calendar dateTime;

    public TimePickerDialogFragment(Calendar dateTime, OnTimeSetListener timeDialogInterface) {
        this.timeDialogInterface = timeDialogInterface;
        this.dateTime = dateTime;
    }

    public TimePickerDialogFragment(OnTimeSetListener timeDialogInterface) {
        this.timeDialogInterface = timeDialogInterface;
    }

    public interface OnTimeSetListener{
        void onResult(Calendar calendar);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (dateTime == null) {
            dateTime = Calendar.getInstance();
        }

        final Calendar currentDate = Calendar.getInstance();

        return new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTime.set(Calendar.MINUTE, minute);
                timeDialogInterface.onResult(dateTime);

                Log.i(TAG, "timePicker: " + dateTime.getTime());
            }
        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
    }
}
