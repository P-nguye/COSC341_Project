package com.example.cosc341_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class YieldReport extends AppCompatActivity {

    private BarChart barChart;
    private DatabaseReference databaseReference;
    private String cropType;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_yield_report);

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        barChart = findViewById(R.id.barChart);
        backButton=findViewById(R.id.backButton);
        // Get crop type from Intent
        cropType = getIntent().getStringExtra("cropType");

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("harvest_info").child(cropType);

        // Fetch data and display bar chart
        fetchYieldData();
        // Handle Back Button
        backButton.setOnClickListener(v -> {
            // Navigate back to HarvestReport
            Intent intent = new Intent(YieldReport.this, HarvestReport.class);
            startActivity(intent);
            finish(); // Optional: Close current activity
        });
    }

    private void fetchYieldData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<Integer, Float> yieldData = new TreeMap<>(); // TreeMap keeps years sorted

                for (DataSnapshot child : snapshot.getChildren()) {
                    String yearStr = child.child("year").getValue(String.class);
                    String yieldStr = child.child("yield").getValue(String.class);

                    if (yearStr != null && yieldStr != null) {
                        try {
                            int year = Integer.parseInt(yearStr);
                            float yield = Float.parseFloat(yieldStr.replaceAll("[^\\d.]", ""));
                            yieldData.put(year, yield);
                        } catch (NumberFormatException e) {
                            Toast.makeText(YieldReport.this, "Error parsing data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if (!yieldData.isEmpty()) {
                    displayBarChart(yieldData);
                } else {
                    Toast.makeText(YieldReport.this, "No data available for " + cropType, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(YieldReport.this, "Failed to fetch data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBarChart(Map<Integer, Float> yieldData) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> years = new ArrayList<>();

        int index = 0;
        for (Map.Entry<Integer, Float> entry : yieldData.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue())); // Use index for BarEntry
            years.add(String.valueOf(entry.getKey())); // Store year as a label
            index++;
        }

        // Create a BarDataSet
        BarDataSet barDataSet = new BarDataSet(entries, "Yield Data");
        barDataSet.setColor(getResources().getColor(android.R.color.holo_blue_light));

        // Create BarData
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f); // Set bar width

        // Set data to the BarChart
        barChart.setData(barData);
        barChart.invalidate(); // Refresh the chart

        // Customize the X-Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(years)); // Set years as X-axis labels
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // Ensure each bar gets its label
        xAxis.setGranularityEnabled(true);

        // Customize the Y-Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f); // Start Y-axis at 0

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right Y-axis

        // Customize the chart description and position
        Description description = new Description();
        description.setText("Yield Report for " + cropType);
        description.setTextSize(14f);
        description.setTextColor(getResources().getColor(android.R.color.black));

        // Position the description at the top
        description.setPosition(barChart.getWidth() / 2f, 50f); // Adjust Y-coordinate as needed
        barChart.setDescription(description);

        // Enable chart animation
        barChart.animateY(1000);
    }
}
