package com.example.cosc341_project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class PlantCropEdit extends AppCompatActivity {
    EditText gardenName, plantName, datePlanted, expected, note;
    private int mYear, mMonth, mDay;
    int gardenIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plant_crop_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        gardenName = (EditText) findViewById(R.id.gardenET);
        plantName = (EditText) findViewById(R.id.nameET);
        datePlanted = (EditText) findViewById(R.id.datePlantedET);
        expected = (EditText) findViewById(R.id.daysET);
        note = (EditText) findViewById(R.id.noteET);

        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            // Get the current date
            LocalDate currentDate = LocalDate.now();
            // Format the current date
            String expectedNumberString = "" +  calculateDaysBetween(bundle.getString("date"), bundle.getString("expected"));
            gardenName.setText(bundle.getString("garden"));
            plantName.setText(bundle.getString("plant"));
            datePlanted.setText(bundle.getString("date"));
            expected.setText(expectedNumberString);
            note.setText(bundle.getString("note"));
            gardenIndex = bundle.getInt("gardenIndex");
        }
    }

    public void showCalendar(View view){ //show calendar when clicked on date text field

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String dayOfMonthString = "" + dayOfMonth;
                        int temp = monthOfYear + 1;
                        String monthOfYearString = "" + temp;
                        if (dayOfMonth < 10){
                            dayOfMonthString = "0" + dayOfMonth;
                        }
                        if (monthOfYear + 1 < 10){

                            monthOfYearString = "0" + temp;
                        }
                        datePlanted.setText(dayOfMonthString + "/" + monthOfYearString + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    public void save(View view) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast;
        Boolean checkValidGrowDate = true;
        if (expected.getText().toString().isEmpty()){
            checkValidGrowDate = false;
        } else if (!expected.getText().toString().matches("\\d+")) {
            checkValidGrowDate = false;
        }
        // Check if the number is positive
        else if (Integer.parseInt(expected.getText().toString()) <= 0) {
            checkValidGrowDate = false;
        }

        if(gardenName.getText().toString().isEmpty()){
            toast = Toast.makeText(this /* MyActivity */, "Please enter a garden name", duration);
            toast.show();
        } else if (plantName.getText().toString().isEmpty()) {
            toast = Toast.makeText(this /* MyActivity */, "Please enter a plant name", duration);
            toast.show();
        } else if (datePlanted.getText().toString().isEmpty()) {
            toast = Toast.makeText(this /* MyActivity */, "Please pick a planted date", duration);
            toast.show();
        } else if (!checkValidGrowDate) {
            toast = Toast.makeText(this /* MyActivity */, "Please enter a positive whole number for grow time", duration);
            toast.show();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            //Calculate the result date.
            LocalDate date = LocalDate.parse(datePlanted.getText().toString(), formatter);
            int days = Integer.parseInt(expected.getText().toString());
            // Add the days to the date
            LocalDate newDate = date.plusDays(days);
            // Format the new date
            String resultDate = newDate.format(formatter);


            // Get the current date
            LocalDate currentDate = LocalDate.now();
            // Format the current date
            String formattedCurrentDate = currentDate.format(formatter);

            long daysLeft = calculateDaysBetween(formattedCurrentDate, resultDate);

            Intent intent = new Intent(this, PlantCropTracker.class);
            Bundle bundle = new Bundle();
            bundle.putString("garden", gardenName.getText().toString());
            bundle.putString("plant", plantName.getText().toString());
            bundle.putString("date", datePlanted.getText().toString());
            bundle.putString("expected", resultDate);
            bundle.putString("daysLeft", "" + daysLeft);
            bundle.putString("note", note.getText().toString());
            bundle.putBoolean("isEditing", true);
            bundle.putInt("gardenIndex", gardenIndex);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
    public static long calculateDaysBetween(String date1, String date2) {
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Parse the input strings into Date objects
        LocalDate startDate = LocalDate.parse(date1, formatter);
        LocalDate endDate = LocalDate.parse(date2, formatter);

        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public void backToTracker(View view){
        finish();
    }
}