package com.example.cosc341_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cosc341_project.api.RetrofitClient;
import com.example.cosc341_project.api.WeatherService;
import com.example.cosc341_project.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherInfo extends AppCompatActivity {
    private TextView headerTextView; // TextView for displaying the city name
    private EditText cityInput;     // EditText for city name input
    private Button fetchWeatherButton,backToMainButton;
    private ScrollView scrollView;
    private LinearLayout scrollLinearLayout;
    //Comment for security purposes
    //Delete BuildConfig.WEATHER_API_KEY and replace with the api key stored in local.properties
    private final String apiKey = BuildConfig.WEATHER_API_KEY;;
    private final String baseUrl = "https://api.openweathermap.org/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        // Initialize UI components
        headerTextView = findViewById(R.id.headerTextView); // Header TextView
        cityInput = findViewById(R.id.cityInput);           // User input for city name
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton); // Button to fetch weather
        scrollView = findViewById(R.id.scrollView);
        scrollLinearLayout = findViewById(R.id.scrollLinearLayout);
        backToMainButton = findViewById(R.id.backToMainButton); // Back to main page button

        // Set fetch weather button click listener
        fetchWeatherButton.setOnClickListener(v -> {
            String cityName = cityInput.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchWeatherData(cityName); // Fetch weather for the entered city
            } else {
                Toast.makeText(this, "Please enter a city name.", Toast.LENGTH_SHORT).show();
            }
        });
        // Set back to main page button click listener
        backToMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(WeatherInfo.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish the current activity to prevent returning to it
        });
    }



    private void fetchWeatherData(String cityName) {
        WeatherService weatherService = RetrofitClient.getClient(baseUrl).create(WeatherService.class);

        Call<WeatherResponse> call = weatherService.getCurrentWeather(cityName, apiKey, "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    updateHeaderTextView(weatherResponse.name);
                    populateWeatherInfo(weatherResponse);
                } else {
                    // Handle invalid city name or other errors
                    String errorMessage = "Invalid city name. Please try again.";
                    if (response.code() == 404) { // 404 indicates city not found
                        errorMessage = "City not found. Please enter a valid city name.";
                    }
                    Toast.makeText(WeatherInfo.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(WeatherInfo.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHeaderTextView(String cityName) {
        // Update the header text with the selected city name
        headerTextView.setText("Weather information of " + cityName);
    }

    private void populateWeatherInfo(WeatherResponse weatherResponse) {
        // Clear previous views in the scroll layout
        scrollLinearLayout.removeAllViews();

        // Add weather details dynamically
        addTextView("City: " + weatherResponse.name);
        addTextView("Temperature: " + weatherResponse.main.temp + "°C");
        addTextView("Feels Like: " + weatherResponse.main.feels_like + "°C");
        addTextView("Humidity: " + weatherResponse.main.humidity + "%");
        addTextView("Pressure: " + weatherResponse.main.pressure + " hPa");
        addTextView("Weather Condition: " + weatherResponse.weather.get(0).description);
        addTextView("Wind Speed: " + weatherResponse.wind.speed + " m/s");
// Add rain information if available
        if (weatherResponse.rain != null) {
            addTextView("Rain (last 1h): " + weatherResponse.rain._1h + " mm");
        }
    }

    private void addTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setPadding(10, 10, 10, 10);
        scrollLinearLayout.addView(textView);
    }
}
