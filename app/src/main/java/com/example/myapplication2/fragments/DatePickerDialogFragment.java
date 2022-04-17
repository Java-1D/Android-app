package com.example.myapplication2.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication2.fragments.DatePickerDialogFragment.OnDateSetListener;

import java.util.Calendar;

/**
 * Date Picker Fragment
 * https://developer.android.com/guide/topics/ui/controls/pickers
 */
public class DatePickerDialogFragment extends DialogFragment {
    final public static String TAG = "DatePickerDialog";
    OnDateSetListener onDateSetListener;
    Calendar dateTime;
    Calendar minDate;
    Calendar maxDate;

    // Used for default getting date
    public DatePickerDialogFragment(OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    // Used to modify an existing calendar
    public DatePickerDialogFragment(OnDateSetListener onDateSetListener, Calendar dateTime) {
        this.onDateSetListener = onDateSetListener;
        this.dateTime = dateTime;
    }

    public void setMinDate(Calendar minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(Calendar maxDate) {
        this.maxDate = maxDate;
    }

    public static interface OnDateSetListener{
        void onResult(Calendar calendar);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (dateTime == null) {
            dateTime = Calendar.getInstance();
        }
        final Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                dateTime.set(year, monthOfYear, dayOfMonth);
                onDateSetListener.onResult(dateTime);
                Log.i(TAG, "datePicker: " + dateTime.getTime());
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        if (minDate != null) {
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }

        if (maxDate != null) {
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        }

        return datePickerDialog;
    }
}