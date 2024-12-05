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

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    Button openCreateCropButton, btnHarvestReport, buttonToGardenTracker, btnToSchedules,btnLogout;
    private String userKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Retrieve the userKey from the intent
        userKey = getIntent().getStringExtra("userKey");
        if (userKey == null) {
            finish(); // If no userKey is provided, return to the login screen
        }
        // Initialize buttons
        openCreateCropButton = findViewById(R.id.openCreateCropButton);
        btnHarvestReport = findViewById(R.id.btnHarvestReport);
        buttonToGardenTracker = findViewById(R.id.toTrackerButton);
        btnToSchedules=findViewById(R.id.toSchedulesButton);
        btnLogout = findViewById(R.id.btnLogout);

        // Set click listeners for both buttons
        openCreateCropButton.setOnClickListener(this);
        btnHarvestReport.setOnClickListener(this);
        buttonToGardenTracker.setOnClickListener(this);
        btnToSchedules.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.openCreateCropButton) {
            // Navigate to CreateCrop activity
            Intent createCropIntent = new Intent(HomePage.this, CreateCrop.class);
            createCropIntent.putExtra("userKey", userKey);
            startActivity(createCropIntent);
        } else if (v.getId() == R.id.btnHarvestReport) {
            // Navigate to HarvestReport activity
            Intent harvestReportIntent = new Intent(HomePage.this, HarvestReport.class);
            harvestReportIntent.putExtra("userKey", userKey);
            startActivity(harvestReportIntent);
        } else if (v.getId() == R.id.weatherInfoButton) {
            // Navigate to WeatherInfo activity
            Intent weatherInfoIntent = new Intent(HomePage.this, WeatherInfo.class);
            weatherInfoIntent.putExtra("userKey", userKey);
            startActivity(weatherInfoIntent);
        } else if (v.getId() == R.id.toTrackerButton) {
            Intent intent = new Intent(this, GardenTracker.class);
            intent.putExtra("userKey", userKey);
            startActivity(intent);
        }
        else if(v.getId()==R.id.toSchedulesButton){
            Intent intent = new Intent(this, ScheduleHub.class);
            intent.putExtra("userKey", userKey);
            startActivity(intent);
        }else if (v.getId() == R.id.btnLogout) {
            // Log out and navigate to MainActivity (Login Page)
            Intent logoutIntent = new Intent(HomePage.this, MainActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        }
    }
}
