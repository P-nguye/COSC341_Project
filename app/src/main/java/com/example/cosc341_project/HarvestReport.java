package com.example.cosc341_project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HarvestReport extends AppCompatActivity {
    private EditText searchEditText;
    private LinearLayout scrollLinearLayout;
    private DatabaseReference databaseReference;
    private ArrayList<String> cropList = new ArrayList<>();
    private ScrollView scrollView;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_harvest_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText);
        scrollView = findViewById(R.id.scrollView);
        scrollLinearLayout = findViewById(R.id.scrollLinearLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference("crops");
        backButton = findViewById(R.id.button);

        fetchCrops();
        // Set up click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity

                finish(); // Optional: Closes the current activity
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCrops(s.toString()); // Filter crops when typing
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void fetchCrops() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cropList.clear(); // Clear existing list
                for (DataSnapshot data : snapshot.getChildren()) {
                    String cropType = data.child("name").getValue(String.class);
                    if (cropType != null) {
                        cropList.add(cropType); // Add crop type to the list
                    }
                }
                populateScrollView(cropList); // Populate the ScrollView
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HarvestReport.this, "Failed to fetch crops: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateScrollView(ArrayList<String> crops) {
        scrollLinearLayout.removeAllViews(); // Clear existing views
        for (String crop : crops) {
            TextView textView = new TextView(this);
            textView.setText(crop);
            textView.setTextSize(18);
            textView.setPadding(10, 10, 10, 10);
            // Set OnClickListener to show popup dialog
            textView.setOnClickListener(v -> showPopupDialog(crop));

            scrollLinearLayout.addView(textView); // Add TextView for each crop
        }
    }

    private void filterCrops(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (String crop : cropList) {
            if (crop.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(crop);
            }
        }
        populateScrollView(filteredList); // Populate with filtered data
    }
    private void showPopupDialog(String cropType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HarvestReport.this);
        builder.setTitle("Harvest Options");
        builder.setMessage("Select an action for " + cropType);

        builder.setPositiveButton("Show Info", (dialog, which) -> showHarvestInfo(cropType));

        builder.setNegativeButton("Add Info", (dialog, which) -> {
            Intent intent = new Intent(HarvestReport.this, HarvestDetail.class);
            intent.putExtra("cropType", cropType);
            startActivity(intent);
        });
        // Show Yield Report option
        builder.setNeutralButton("Show Yield Report", (dialog, which) -> {
            Toast.makeText(HarvestReport.this, "Navigating to Yield Report", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HarvestReport.this, YieldReport.class);
            intent.putExtra("cropType", cropType);
            startActivity(intent);
        });


        //builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void showHarvestInfo(String cropType) {
        DatabaseReference harvestInfoRef = FirebaseDatabase.getInstance().getReference("harvest_info").child(cropType);

        harvestInfoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();

                if (snapshot.exists()) {
                    StringBuilder harvestInfo = new StringBuilder();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        String year = child.child("year").getValue(String.class);
                        String yield = child.child("yield").getValue(String.class);

                        if (year != null && yield != null) {
                            harvestInfo.append("Year: ").append(year).append("\n");
                            harvestInfo.append("Yield: ").append(yield).append("\n\n");
                        }
                    }

                    // If harvestInfo is populated, show it in the dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(HarvestReport.this);
                    builder.setTitle("Harvest Information for " + cropType);
                    builder.setMessage(harvestInfo.toString());
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    builder.show();

                } else {
                    // No harvest information found
                    showNoInfoDialog(cropType);
                }
            } else {
                Toast.makeText(HarvestReport.this, "Failed to fetch harvest info. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showNoInfoDialog(String cropType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HarvestReport.this);
        builder.setTitle("Harvest Information for " + cropType);
        builder.setMessage("No information has been added for this crop.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
