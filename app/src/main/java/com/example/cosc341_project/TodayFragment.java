package com.example.cosc341_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TodayFragment extends BaseFragment {
    @Override
    protected boolean filterSchedule(Schedule schedule) {
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(today.getTime()).equals(schedule.getDate());
    }

}