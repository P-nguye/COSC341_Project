package com.example.cosc341_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class HarvestDetail extends AppCompatActivity {
    private EditText yieldEditText, yearEditText;
    private Button saveButton, backButton;
    private DatabaseReference databaseReference;
    private String cropType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_harvest_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize UI components
        yieldEditText = findViewById(R.id.yieldEditText);
        yearEditText = findViewById(R.id.yearEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
    // Get crop type from Intent
        cropType = getIntent().getStringExtra("cropType");

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("harvest_info");

        // Save button listener
        saveButton.setOnClickListener(v -> saveHarvestInfo());

        // Back button listener
        backButton.setOnClickListener(v -> finish());
    }
    private void saveHarvestInfo() {
        String yield = yieldEditText.getText().toString().trim();
        String year = yearEditText.getText().toString().trim();

        if (TextUtils.isEmpty(yield) || TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!year.matches("\\d{4}")) {
            Toast.makeText(this, "Please enter a valid year (e.g., 2024)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data to Firebase
        String id = databaseReference.push().getKey();
        if (id != null) {
            Map<String, Object> harvestData = new HashMap<>();
            harvestData.put("id", id);
            harvestData.put("cropType", cropType);
            harvestData.put("yield", yield);
            harvestData.put("year", year);

            databaseReference.child(cropType).child(id).setValue(harvestData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(HarvestDetail.this, "Harvest info added successfully", Toast.LENGTH_SHORT).show();
                            yieldEditText.setText("");
                            yearEditText.setText("");
                        } else {
                            Toast.makeText(HarvestDetail.this, "Failed to add harvest info", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}