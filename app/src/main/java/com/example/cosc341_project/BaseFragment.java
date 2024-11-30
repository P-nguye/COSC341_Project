package com.example.cosc341_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BaseFragment extends Fragment {
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private ArrayList<Schedule> scheduleList;
    private DatabaseReference db;

    //Override the original view create to implement the recycler view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);

        recyclerView = view.findViewById(R.id.recycler_vw);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        scheduleList = new ArrayList<>();
        //Get the iterator for the schedules
        adapter = new ScheduleAdapter(scheduleList);
        recyclerView.setAdapter(adapter);
        //initialize db
        db = FirebaseDatabase.getInstance().getReference("schedules");
        //get the schedule data from db
        fetchSchedules();
        return view;
    }

    private void fetchSchedules() {
        //Recieve updates about data changes
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //When data is changes, clear the list and refresh it
                scheduleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Schedule schedule = dataSnapshot.getValue(Schedule.class);
                    //Add the schedule only if it matches the filter requirements for the tab
                    if (schedule != null && filterSchedule(schedule)) {
                        scheduleList.add(schedule);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load schedules: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Override this method in child fragments for filtering
    protected boolean filterSchedule(Schedule schedule) {
        return true;
    }
}