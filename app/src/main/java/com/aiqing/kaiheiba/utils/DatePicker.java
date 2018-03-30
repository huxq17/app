package com.aiqing.kaiheiba.utils;

import android.app.Activity;
import android.app.DatePickerDialog;

import java.util.Calendar;

public class DatePicker {

    private Activity context;
    private DatePickerDialog.OnDateSetListener listener;

    public DatePicker(Activity context, DatePickerDialog.OnDateSetListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show() {
        Calendar ca = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, listener,
                ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
