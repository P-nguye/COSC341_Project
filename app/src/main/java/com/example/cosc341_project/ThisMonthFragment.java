package com.example.cosc341_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;


public class ThisMonthFragment extends BaseFragment {
    @Override
    protected boolean filterSchedule(Schedule schedule) {
        Calendar today = Calendar.getInstance();
        Calendar scheduleDate = Calendar.getInstance();
        String[] dateParts = schedule.getDate().split("/");

        if (dateParts.length == 3) {
            scheduleDate.set(Integer.parseInt(dateParts[2]),
                    Integer.parseInt(dateParts[1]) - 1,
                    Integer.parseInt(dateParts[0]));

            int thisMonth = today.get(Calendar.MONTH);
            int thisYear = today.get(Calendar.YEAR);
            int scheduleMonth = scheduleDate.get(Calendar.MONTH);
            int scheduleYear = scheduleDate.get(Calendar.YEAR);
            // Return the parts where this month
            return thisMonth == scheduleMonth && thisYear == scheduleYear;
        }
        return false;
    }
}