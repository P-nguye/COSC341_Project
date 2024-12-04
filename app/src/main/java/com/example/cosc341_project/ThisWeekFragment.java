package com.example.cosc341_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ThisWeekFragment extends BaseFragment {
    @Override
    protected boolean filterSchedule(Schedule schedule) {
        Calendar today = Calendar.getInstance();
        Calendar scheduleDate = Calendar.getInstance();
        //Split up the date such that we get the week
        String[] dateParts = schedule.getDate().split("/");

        if (dateParts.length == 3) {
            scheduleDate.set(Integer.parseInt(dateParts[2]),
                    Integer.parseInt(dateParts[1]) - 1,
                    Integer.parseInt(dateParts[0]));

            int todayWeek = today.get(Calendar.WEEK_OF_YEAR);
            int scheduleWeek = scheduleDate.get(Calendar.WEEK_OF_YEAR);
            int todayYear = today.get(Calendar.YEAR);
            int scheduleYear = scheduleDate.get(Calendar.YEAR);

            return todayWeek == scheduleWeek && todayYear == scheduleYear;
        }
        return false;
    }
}