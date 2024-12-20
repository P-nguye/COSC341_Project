package com.example.cosc341_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CreateSchedule extends AppCompatActivity {

    private EditText etTitle, etNotes;
    private TextView tvDate, tvTime;
    private Spinner spinnerGarden, spinnerRepeat;
    private CheckBox cbReminder;
    private Button btnSubmit, btnCancel;
    private ImageButton btnDate, btnTime;
    private String userKey;
    private String selectedDate = "";
    private String selectedTime = "";

    private DatabaseReference db;
    private boolean edited;
    ArrayAdapter<CharSequence> repeatAdapter;
    ArrayAdapter<String> gardenAdapter;
    Context context=this;
    ArrayList<String> gardenNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Retrieve userKey from Intent
        userKey = getIntent().getStringExtra("userKey");
        if (userKey == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //Initialize db
        db = FirebaseDatabase.getInstance().getReference("users").child(userKey);

        //Init UI
        etTitle=(EditText) findViewById(R.id.schedule_title);
        etNotes=(EditText) findViewById(R.id.notes_et);
        tvDate=(TextView) findViewById(R.id.selected_date_txt);
        tvTime=(TextView) findViewById(R.id.selected_time_txt);
        spinnerGarden=(Spinner) findViewById(R.id.garden_select_spnr);
        spinnerRepeat=(Spinner) findViewById(R.id.repeat_spnr);
        cbReminder=(CheckBox) findViewById(R.id.receiveReminder);
        btnSubmit=(Button) findViewById(R.id.save_schedule_btn);
        btnCancel=(Button) findViewById(R.id.create_task_cancel);
        btnDate=(ImageButton) findViewById(R.id.date_btn);
        btnTime= (ImageButton) findViewById(R.id.time_btn);

        // Populate spinners
        populateSpinners();
        Log.d("ScheduleCreate", "AfterPopSpinners: "+gardenNames.toString());
        //Check if it is being edited or not:
        edited=getIntent().getBooleanExtra("Editing", false);

        //Set buttons
        // Date picker
        btnDate.setOnClickListener(v -> showDatePicker());

        // Time picker
        btnTime.setOnClickListener(v -> showTimePicker());

        // Submit button
        btnSubmit.setOnClickListener(v -> saveSchedule());
        
        //Cancel button
        btnCancel.setOnClickListener(v-> cancelSchedule());
    }

    private void cancelSchedule() {
        Intent intent = new Intent(CreateSchedule.this, ScheduleHub.class);
        intent.putExtra("userKey", userKey); // Pass userKey back to ScheduleHub

        startActivity(intent);
    }

    private void saveSchedule() {
        String title = etTitle.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String garden = spinnerGarden.getSelectedItem() != null ? spinnerGarden.getSelectedItem().toString() : "";
        String repeat = spinnerRepeat.getSelectedItem() != null ? spinnerRepeat.getSelectedItem().toString() : "";

        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        String id;
        HashMap<String, Object> schedule = new HashMap<>();
        schedule.put("title", title);
        schedule.put("date", selectedDate);
        schedule.put("time", selectedTime);
        schedule.put("garden", garden);
        schedule.put("repeat", repeat);
        schedule.put("notes", notes);
        schedule.put("reminder", cbReminder.isChecked());
        if (edited) {
            // Update the existing entry
            id=getIntent().getStringExtra("Schedule");
            if(id!=null){
                schedule.put("id", id);
                db.child("schedules").child(id).updateChildren(schedule)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Schedule updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ScheduleHub.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to Save Schedule", Toast.LENGTH_SHORT).show();
                        });
            }

        }
        else{
            // Save to Firebase
            id = db.child("schedules").push().getKey();
            if(id!=null) {
                schedule.put("id", id);
                db.child("schedules").child(id).setValue(schedule)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CreateSchedule.this, "Schedule saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ScheduleHub.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Toast.makeText(CreateSchedule.this, "Failed to save schedule", Toast.LENGTH_SHORT).show());
            }
        }

    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        //Set a time picker dialogue for when button is clicked
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
            //set the text to selected time
            tvTime.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        // Get current date values
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectyear, selectmonth, dayOfMonth) -> {
            // Update calendar with selected date
            calendar.set(Calendar.YEAR, selectyear);
            calendar.set(Calendar.MONTH, selectmonth);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Format date and set to TextView
            updateDateLabel(calendar);
        },
                year, month, day
        ).show();
    }
    private void updateDateLabel(Calendar c) {
        // Format the date (e.g., "MMM dd, yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        selectedDate = dateFormat.format(c.getTime());
        tvDate.setText(selectedDate);
    }
    private void populateSpinners() {
        //Set the repetition adapter
        repeatAdapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_options, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(repeatAdapter);

        //Set the crop spinner
        db.child("gardens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gardenNames.clear();
                for (DataSnapshot cropSnapshot : snapshot.getChildren()) {
                    // Get the "name" field from Firebase
                    String cropName = cropSnapshot.child("name").getValue(String.class);
                    if (cropName != null) {
                        gardenNames.add(cropName);
                    }
                }
                if (gardenNames.isEmpty()) {
                    gardenNames.add("No gardens available"); // Default option
                }

                // Set the spinner adapter
                gardenAdapter = new ArrayAdapter<>(CreateSchedule.this,
                        android.R.layout.simple_spinner_item, gardenNames);
                gardenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGarden.setAdapter(gardenAdapter);
                ///Must setup pre-edit after the spinner is setup
                if(edited){
                    prepopulateForm(getIntent().getStringExtra("Schedule"));
                    Log.d("ScheduleCreate", "EditWas called "+gardenNames.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateSchedule.this, "Failed to load garden data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void prepopulateForm(String id){
        // Editing an existing entry
        db.child("schedules").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Populate the form fields with existing data
                    etTitle.setText(snapshot.child("title").getValue(String.class));
                    etNotes.setText(snapshot.child("notes").getValue(String.class));
                    if(snapshot.child("date").getValue(String.class)!=null){
                        selectedDate=snapshot.child("date").getValue(String.class);
                        tvDate.setText(selectedDate);
                    }
                    if(snapshot.child("time").getValue(String.class)!=null){
                        selectedTime=snapshot.child("time").getValue(String.class);
                        tvTime.setText(selectedTime);
                    }
                    String repeat =snapshot.child("repeat").getValue(String.class);
                    if(repeat!=null){
                        int pos=repeatAdapter.getPosition(repeat);
                        spinnerRepeat.setSelection(pos);
                    }
                    String garden = snapshot.child("garden").getValue(String.class);
                    if(!garden.equals("No gardens available")){
                        int pos=gardenAdapter.getPosition(garden);
                        spinnerGarden.setSelection(pos);


                    }
                    boolean r= Boolean.TRUE.equals(snapshot.child("reminder").getValue(Boolean.class));
                        cbReminder.setChecked(r);
                }
                else{
                    Toast.makeText(context, "Data not found for this ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}