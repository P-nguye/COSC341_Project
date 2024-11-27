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

public class PlantCropTracker extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> parentList;
    private HashMap<String, List<String>> childList;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plant_crop_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        expandableListView = findViewById(R.id.expandableListView);

        parentList = new ArrayList<>();
        childList = new HashMap<>();
        String data = "";
        String input = "";
        String filename = "output.txt";

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
                data += (counter + 1) + "\t" + line + "\n";
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
            br.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        // Set up the adapter
        adapter = new ExpandableListAdapter(this, parentList, childList);
        expandableListView.setAdapter(adapter);

        // Set the child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Get the child item name
                String childName = childList.get(parentList.get(groupPosition)).get(childPosition);

                // Show a Toast message
//                Toast.makeText(PlantCropTracker.this, "Clicked: " + childName + childPosition, Toast.LENGTH_SHORT).show();
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

                Intent intent = new Intent(PlantCropTracker.this, PlantCropEdit.class);
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
        Intent intent = new Intent(this, PlantCropAdd.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if the result is from SecondActivity
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && intent != null) {
            // Retrieve the new data
            Bundle bundle = intent.getExtras();

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

                    System.out.println("File updated successfully!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                parentList.set(index, bundle.getString("garden"));
                childList.put(parentList.get(bundle.getInt("gardenIndex")), childTemp);
            }
            else {
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
                parentList.add(bundle.getString("garden"));
                childList.put(parentList.get(parentList.size() - 1), childTemp);
            }
            adapter = new ExpandableListAdapter(this, parentList, childList);
            expandableListView.setAdapter(adapter);

        }
    }
}