package com.example.cosc341_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class ScheduleHub extends AppCompatActivity {

    private TabLayout tabLayout;
    private Button btnHome, btnNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_hub);
        btnHome= findViewById(R.id.home_btn);
        btnNew = findViewById(R.id.add_task_btn);
        tabLayout = findViewById(R.id.task_tabs);
        //Load the fragment as today
        loadFragment(new TodayFragment());

        // Tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                Fragment selectedFragment = null;

                switch (tab.getPosition()) {
                    case 1:
                        selectedFragment = new ThisWeekFragment();
                        break;
                    case 2:
                        selectedFragment = new ThisMonthFragment();
                        break;
                    case 3:
                        selectedFragment = new AllSchedulesFragment();
                        break;
                    default:
                        selectedFragment = new TodayFragment();
                        break;
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
            }
        });

        //Cancel button
        btnHome.setOnClickListener(v-> back());

        //Add Task button
        btnNew.setOnClickListener(v-> createNewSchedule());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.commit();
    }

    private void back() {
        Intent intent = new Intent(ScheduleHub.this, MainActivity.class);
        startActivity(intent);
    }

    private void createNewSchedule(){
        Intent intent = new Intent(ScheduleHub.this, CreateSchedule.class);
        startActivity(intent);
    }

}