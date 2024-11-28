package com.example.cosc341_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GardenTracker extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> parentList;
    private HashMap<String, List<String>> childList;
    private static final int REQUEST_CODE = 1;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_garden_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("gardens");
        // Initialize UI components
        expandableListView = findViewById(R.id.expandableListView);

        parentList = new ArrayList<>();
        childList = new HashMap<>();
        String filename = "output.txt";
//        getGarden(filename);
        getGardenDatabase();

        // Set the child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Get the child item name
                String childName = childList.get(parentList.get(groupPosition)).get(childPosition);

                // Show a Toast message
                Toast.makeText(GardenTracker.this, "Clicked: " + childName + childPosition, Toast.LENGTH_SHORT).show();
                String[] parts = new String[2];
                String garden = parentList.get(groupPosition).toString();
                String plant = childList.get(parentList.get(groupPosition)).get(0);
                parts = plant.split(": ");
                plant = parts[1];
                String date = childList.get(parentList.get(groupPosition)).get(1);
                parts = date.split(": ");
                date = parts[1];
                String expected = childList.get(parentList.get(groupPosition)).get(2);
                parts = expected.split(": ");
                expected = parts[1];
                String note = childList.get(parentList.get(groupPosition)).get(4);
                parts = note.split(": ");
                if (parts.length > 1) {
                    note = parts[1];
                } else {
                    note = "";
                }

                Intent intent = new Intent(GardenTracker.this, GardenEdit.class);
                Bundle bundle = new Bundle();
                bundle.putInt("gardenIndex", groupPosition);
                bundle.putString("garden", garden);
                bundle.putString("plant", plant);
                bundle.putString("date", date);
                bundle.putString("expected", expected);
                bundle.putString("note", note);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            }
        });
    }
    public void backToMain(View view){
        finish();
    }
    public void addGarden(View view){
        Intent intent = new Intent(this, GardenAdd.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if the result is from SecondActivity
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && intent != null) {
            // Retrieve the new data
            Bundle bundle = intent.getExtras();

            if(bundle.getBoolean("isDeleting")){
                deleteGarden(bundle.getInt("gardenIndex"));
            }
            else{
                // Update the UI with the new data

                List<String> childTemp = new ArrayList<>();
                String plant = "Plant Name: " + bundle.getString("plant");
                String date = "Date Planted: " + bundle.getString("date");
                String expected = "Expected to Grow by: " + bundle.getString("expected");
                String daysLeft = "Days left until full growth: " + bundle.getString("daysLeft");
                String note = "Note: " + bundle.getString("note");

                childTemp.add(plant);
                childTemp.add(date);
                childTemp.add(expected);
                childTemp.add(daysLeft);
                childTemp.add(note);

                String filename = "output.txt";

                String fileContents = bundle.getString("garden") + ","
                        + plant + ","
                        + date + ","
                        + expected + ","
                        + daysLeft + ","
                        + note + "e_nd/O_f//L_ine\n";           //Code for line end, I make it as random as possible

                if (bundle.getBoolean("isEditing")) {
                    int index = bundle.getInt("gardenIndex");
//                editGarden(index, filename, fileContents);
                    updateGardenDatabase(index, bundle.getString("garden"), plant, date, expected, daysLeft, note);
                    parentList.set(index, bundle.getString("garden"));
                    childList.put(parentList.get(bundle.getInt("gardenIndex")), childTemp);
                }
                else {
//                addGarden(filename, fileContents);
                    updateGardenDatabase(parentList.size(), bundle.getString("garden"), plant, date, expected, daysLeft, note);
                    parentList.add(bundle.getString("garden"));
                    childList.put(parentList.get(parentList.size() - 1), childTemp);
                    for (String garden: parentList) {
                        Log.d("Test", garden);
                    }
                }
//            adapter = new ExpandableListAdapter(this, parentList, childList);
//            expandableListView.setAdapter(adapter);
            }

        }
    }
    private void editGarden(int index, String filename,String fileContents ){
        try {
            FileInputStream fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            // It creates a way to convert the raw data from the file into human-readable text.
            BufferedReader br = new BufferedReader(isr);
            StringBuilder newEditString = new StringBuilder();

            int currentLine = 0;
            byte[] stringByte = new byte[fis.available()];
            fis.read(stringByte);
            fis.close();
            // Convert to a single string
            String content = new String(stringByte);
            // Split the content by the custom delimiter
            String[] lines = content.split("e_nd/O_f//L_ine\n");        //Code for line end, I make it as random as possible


            // Read and modify the specific line
            for (String line : lines) {
                Log.d("line", line);
                Log.d("currentLine", currentLine+"");
                Log.d("index", index + "");
                if (currentLine == index) {
                    newEditString.append(fileContents);
                } else {
                    newEditString.append(line).append("e_nd/O_f//L_ine\n");     //Code for line end, I make it as random as possible
                }
                currentLine++;
            }
            br.close();
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(newEditString.toString().getBytes());
            outputStream.close();

//            System.out.println("File updated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addGarden(String filename, String fileContents){
        FileOutputStream outputStream;
        //allow a file to be opened for writing
        try {
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            //This opens or creates a file with the given filename
            // MODE_APPEND-> add data to the end of the file (file already exists)
            // If the file doesn't exist, a new one will be created.
            outputStream.write(fileContents.getBytes());
            // Converts the file Contents from text (like words and sentences) into bytes.
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getGarden(String filename){
        String input;
        String data;
        try {
            FileInputStream fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            // It creates a way to convert the raw data from the file into human-readable text.
            BufferedReader br = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            int counter = 0;
            byte[] stringByte = new byte[fis.available()];
            fis.read(stringByte);
            fis.close();
            // Convert to a single string
            String content = new String(stringByte);
            // Split the content by the custom delimiter
            String[] lines = content.split("e_nd/O_f//L_ine\n");    //Code for line end, I make it as random as possible
            for (String line : lines) {
                // This condition checks whether the line read from the file is not empty
//                data += (counter + 1) + "\t" + line + "\n";
                input = line + "\n";
                Log.d("key",line);

                String[] temp = input.split(",");
                List<String> childTemp = new ArrayList<>();

                parentList.add(temp[0]);
                childTemp.add(temp[1]);
                childTemp.add(temp[2]);
                childTemp.add(temp[3]);
                childTemp.add(temp[4]);
                childTemp.add(temp[5]);
                childList.put(temp[0], childTemp);

//                stringBuilder.append(line).append("\n");
                counter++;
            }
            populateExpandableList(parentList, childList);
            br.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void getGardenDatabase() {      //This method run whenever there is a change in the databased (Useful for adding, editing)
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                parentList.clear();
                childList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    //Store data
                    String gardenName = data.child("name").getValue(String.class);
                    String plant = data.child("plant").getValue(String.class);
                    String date = data.child("date").getValue(String.class);
                    String expected = data.child("expected").getValue(String.class);
                    String daysLeft = data.child("daysLeft").getValue(String.class);
                    String note = data.child("note").getValue(String.class);
//                    Log.d("name", gardenName);
//                    Log.d("plant", plant);

                    //Put data in a list to prepare for showing
                    parentList.add(gardenName);
                    List<String> childTemp = new ArrayList<>();
                    childTemp.add(plant);
                    childTemp.add(date);
                    childTemp.add(expected);
                    childTemp.add(daysLeft);
                    childTemp.add(note);
                    childList.put(gardenName, childTemp);
                }
                populateExpandableList(parentList, childList);
//                Toast.makeText(GardenTracker.this, "Done", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(GardenTracker.this, "Failed to get garden data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateGardenDatabase(int id, String gardenName, String plant, String date, String expected, String daysLeft, String note){

        // Create a Map for data
        Map<String, Object> gardenData = new HashMap<>();
        gardenData.put("gardenID", id);
        gardenData.put("name", gardenName);
        gardenData.put("plant", plant);
        gardenData.put("date", date);
        gardenData.put("expected", expected);
        gardenData.put("daysLeft", daysLeft);
        gardenData.put("note", note);

        // Store data in Firebase
        databaseReference.child(id+"").setValue(gardenData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Gardens update successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add crop", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void populateExpandableList(List<String> parentList, HashMap<String, List<String>> childList){
        // Set up the adapter
        adapter = new ExpandableListAdapter(GardenTracker.this, parentList, childList);
        expandableListView.setAdapter(adapter);
    }
    private void deleteGarden(int idToDelete) {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();

                // Delete the specific item
                databaseReference.child(String.valueOf(idToDelete)).removeValue();
//                Log.d("idTODelete", ""+ idToDelete);

                // Reorder the IDs of subsequent items
                for (DataSnapshot item : snapshot.getChildren()) {
                    int currentId = Integer.parseInt(item.getKey());
//                    Log.d("key", item.getKey());
                    if (currentId > idToDelete) {
                        Map<String, Object> currentItem = (Map<String, Object>) item.getValue();

                        // Update the ID to currentId - 1
                        databaseReference.child(String.valueOf(currentId)).removeValue();
                        databaseReference.child(String.valueOf(currentId - 1)).setValue(currentItem);
                    }
                }
            }
        });
    }

}