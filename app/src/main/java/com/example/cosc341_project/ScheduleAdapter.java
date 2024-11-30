package com.example.cosc341_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private ArrayList<Schedule> scheduleList;

    public ScheduleAdapter(ArrayList<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }
    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        // Get the current schedule
        Schedule schedule = scheduleList.get(position);

        // Bind data to the views
        holder.tvTitle.setText(schedule.getTitle());
        holder.tvDateTime.setText(schedule.getDate() + " " + (schedule.getTime() == null ? "" : schedule.getTime()));
        holder.tvGardenName.setText(schedule.getGarden() == null ? "No Garden Selected" : schedule.getGarden());
        holder.tvNotes.setText(schedule.getNotes() == null ? "No Notes" : schedule.getNotes());
        holder.btnEdit.setOnClickListener(v ->{
            Context context = v.getContext();
            Intent intent = new Intent(context, CreateSchedule.class);

            //Launch an editable intent that when flagged: Prepopulates the values
            intent.putExtra("Editing", true);
            intent.putExtra("Schedule", schedule.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    //Create the generic placeholder displaying all of the data for the schedule view holder
    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateTime, tvGardenName, tvNotes;
        Button btnEdit;
        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.title_tv);
            tvDateTime = itemView.findViewById(R.id.date_time_tv);
            tvGardenName = itemView.findViewById(R.id.gardenname_tv);
            tvNotes = itemView.findViewById(R.id.notes_tv);
            btnEdit = itemView.findViewById(R.id.edit_btn);
        }
    }
}
