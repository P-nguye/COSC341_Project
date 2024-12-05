package com.example.cosc341_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class CreateCrop extends AppCompatActivity implements View.OnClickListener {
    private EditText cropName, cropType, cropQuantity;
    private Button createCropButton, backToMainButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_crop);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("crops");

        // Initialize UI components
        cropName = findViewById(R.id.cropName);
        cropType = findViewById(R.id.cropType);
        cropQuantity = findViewById(R.id.cropQuantity);
        createCropButton = findViewById(R.id.createCropButton);
        backToMainButton = findViewById(R.id.backToMainButton);

        // Set click listeners
        createCropButton.setOnClickListener(this);
        backToMainButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createCropButton) {
            // Handle Create com.example.cosc341_project.Crop button click
            String name = cropName.getText().toString().trim();
            String type = cropType.getText().toString().trim();
            String quantityStr = cropQuantity.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(type) || TextUtils.isEmpty(quantityStr)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                int quantity = Integer.parseInt(quantityStr);
                checkForDuplicateCrop(name, type, quantity);            }
        } else if (v.getId() == R.id.backToMainButton) {
            // Handle Back to Main button click

            finish(); // Optional: Closes the current activity
        }
    }
    private void checkForDuplicateCrop(String name, String type, int quantity) {
        // Query the database to check if the crop name already exists
        databaseReference.orderByChild("name").equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // com.example.cosc341_project.Crop name already exists
                            Toast.makeText(CreateCrop.this, "A crop with this name already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            // No duplicates found, proceed to create crop
                            createCrop(name, type, quantity);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CreateCrop.this, "Failed to check for duplicates: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

                    private void createCrop(String name, String type, int quantity) {
        // Generate a unique key for the crop
        String cropId = databaseReference.push().getKey();

        if (cropId != null) {
            // Create a crop object or use a Map for data
            Map<String, Object> cropData = new HashMap<>();
            cropData.put("id", cropId);
            cropData.put("name", name);
            cropData.put("type", type);
            cropData.put("quantity", quantity);

            // Store data in Firebase
            databaseReference.child(cropId).setValue(cropData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "com.example.cosc341_project.Crop added successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields
                    cropName.setText("");
                    cropType.setText("");
                    cropQuantity.setText("");
                } else {
                    Toast.makeText(this, "Failed to add crop", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Error generating crop ID", Toast.LENGTH_SHORT).show();
        }
    }
}
