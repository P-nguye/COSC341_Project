package com.example.cosc341_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button openCreateCropButton, btnHarvestReport, buttonToGardenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        openCreateCropButton = findViewById(R.id.openCreateCropButton);
        btnHarvestReport = findViewById(R.id.btnHarvestReport);
        buttonToGardenTracker = findViewById(R.id.toTrackerButton);

        // Set click listeners for both buttons
        openCreateCropButton.setOnClickListener(this);
        btnHarvestReport.setOnClickListener(this);
        buttonToGardenTracker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.openCreateCropButton) {
            // Navigate to CreateCrop activity
            Intent createCropIntent = new Intent(MainActivity.this, CreateCrop.class);
            startActivity(createCropIntent);
        } else if (v.getId() == R.id.btnHarvestReport) {
            // Navigate to HarvestReport activity
            Intent harvestReportIntent = new Intent(MainActivity.this, HarvestReport.class);
            startActivity(harvestReportIntent);
        } else if (v.getId() == R.id.toTrackerButton) {
            Intent intent = new Intent(this, GardenTracker.class);
            startActivity(intent);
        }
    }
}
