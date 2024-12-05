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
    private String userKey;

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
        userKey = getIntent().getStringExtra("userKey");
        if (userKey == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText);
        scrollView = findViewById(R.id.scrollView);
        scrollLinearLayout = findViewById(R.id.scrollLinearLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userKey).child("crops");
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

        builder.setNegativeButton("Add Info/Delete crop", (dialog, which) -> {
            Intent intent = new Intent(HarvestReport.this, HarvestDetail.class);
            addOrDelete(cropType);
            intent.putExtra("userKey", userKey);
        });
        // Show Yield Report option
        builder.setNeutralButton("Show Yield Report", (dialog, which) -> {
            Toast.makeText(HarvestReport.this, "Navigating to Yield Report", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HarvestReport.this, YieldReport.class);
            intent.putExtra("cropType", cropType);
            intent.putExtra("userKey", userKey);
            startActivity(intent);
        });


        //builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void addOrDelete(String cropType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HarvestReport.this);
        builder.setTitle("Add or Delete?");
        builder.setMessage("Do you want to add info or delete "  + cropType + "?");
        builder.setPositiveButton("Add", (dialog, which) -> {
            Intent intent = new Intent(this, HarvestDetail.class);
            intent.putExtra("cropType", cropType);
            intent.putExtra("userKey", userKey);
            startActivity(intent);

        });
        builder.setNegativeButton("Delete", (dialog, which) -> {
            delete(cropType);

//            Toast.makeText(GardenEdit.this, "No", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
    private void delete(String cropType){
        AlertDialog.Builder builder = new AlertDialog.Builder(HarvestReport.this);
        builder.setTitle("Warning!!!");
        builder.setMessage("Are you sure you want to delete " + cropType + " ?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            databaseReference.orderByChild("name").equalTo(cropType).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Iterate through all children with the matching "name"
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        // Remove the child
                        childSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Deletion failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
//            Toast.makeText(HarvestReport.this, cropType + " Deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
//            Toast.makeText(GardenEdit.this, "No", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
    private void showHarvestInfo(String cropType) {
        DatabaseReference harvestInfoRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userKey)
                .child("harvest_info")
                .child(cropType);

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
