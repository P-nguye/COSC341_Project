package com.example.cosc341_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CropView extends AppCompatActivity {
    Button addBtn, homeBtn;
    CropAdapter adapter;
    DatabaseReference db;
    RecyclerView rView;
    ArrayList<Crop> items = new ArrayList<>();
    String userKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crop_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userKey=getIntent().getStringExtra("userKey");
        homeBtn = findViewById(R.id.home_Crops_btn);
        addBtn = findViewById(R.id.create_crop_btn);
        rView = findViewById(R.id.crop_recycler);
        rView.setLayoutManager(new LinearLayoutManager(this));
;
        adapter = new CropAdapter(this, items, userKey);
        rView.setAdapter(adapter);
        db = FirebaseDatabase.getInstance().getReference("users").child(userKey).child("crops");
        fetchCrops();

        addBtn.setOnClickListener(v->{
            Intent createCropIntent = new Intent(CropView.this, CreateCrop.class);
            createCropIntent.putExtra("userKey", userKey);
            startActivity(createCropIntent);
        });
        homeBtn.setOnClickListener(v->{
            Intent homeIntent = new Intent(CropView.this, HomePage.class);
            homeIntent.putExtra("userKey", userKey);
            startActivity(homeIntent);
        });
    }
    private void fetchCrops() {
        //Recieve updates about data changes
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //When data is changes, clear the list and refresh it
                items.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Crop crop = dataSnapshot.getValue(Crop.class);
                    if(crop!=null){
                        items.add(crop);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), "Failed to load Crops: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}