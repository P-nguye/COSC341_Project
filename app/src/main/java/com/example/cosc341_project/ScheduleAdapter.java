package com.example.cosc341_project;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private Context context;
    private DatabaseReference db;
    private ArrayList<Schedule> scheduleList;
    private String userKey;

    public ScheduleAdapter(Context context, ArrayList<Schedule> scheduleList, String userKey) {
        this.context=context;
        this.scheduleList = scheduleList;
        this.userKey=userKey;
        this.db= FirebaseDatabase.getInstance().getReference("users").child(userKey).child("schedules");
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
            intent.putExtra("userKey", userKey);
            context.startActivity(intent);
        });
        holder.btnDelete.setOnClickListener(v ->{
            showDeletePrompt(schedule, position);
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    private void deleteSchedule(Schedule schedule, int position) {
        // Remove from Firebase
        String scheduleId = schedule.getId(); // Ensure Schedule class has an `id` field
        if (scheduleId != null) {
            db.child(scheduleId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        if(position<scheduleList.size()){
                            // Remove from the list and notify adapter
                            notifyItemRemoved(position);
                        }

                        Toast.makeText(context, "Schedule deleted successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to delete schedule: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "Schedule ID is null, cannot delete.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeletePrompt(Schedule schedule, int position) {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Delete Schedule")
                .setMessage("Are you sure you want to delete this schedule?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove schedule from Firebase and the list
                    deleteSchedule(schedule, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    //Create the generic placeholder displaying all of the data for the schedule view holder
    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateTime, tvGardenName, tvNotes;
        Button btnEdit, btnDelete;
        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.crop_name_tv);
            tvDateTime = itemView.findViewById(R.id.date_time_tv);
            tvGardenName = itemView.findViewById(R.id.cropType_tv);
            tvNotes = itemView.findViewById(R.id.crop_qty_tv);
            btnEdit = itemView.findViewById(R.id.edit_crop_btn);
            btnDelete = itemView.findViewById(R.id.delete_crop_btn);
        }
    }

}
