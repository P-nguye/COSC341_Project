package com.example.cosc341_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ForgotPassword extends AppCompatActivity {

    private EditText etEmail, etConfirmationCode, etNewPassword;
    private Button btnSendCode, btnVerifyCode, btnResetPassword;
    private DatabaseReference databaseReference;

    private String generatedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize UI elements
        etEmail = findViewById(R.id.etEmail);
        etConfirmationCode = findViewById(R.id.etConfirmationCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Handle Send Code button
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConfirmationCode();
            }
        });

        // Handle Verify Code button
        btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });

        // Handle Reset Password button
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void sendConfirmationCode() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate random 6-digit code
        Random random = new Random();
        generatedCode = String.format("%06d", random.nextInt(1000000));

        // Here, you would send the confirmation code via email.
        // For simplicity, we'll simulate this with a Toast message.
        Toast.makeText(this, "Confirmation code sent: " + generatedCode, Toast.LENGTH_SHORT).show();

        // Enable the confirmation code field and button
        etConfirmationCode.setVisibility(View.VISIBLE);
        btnVerifyCode.setVisibility(View.VISIBLE);
    }

    private void verifyCode() {
        String enteredCode = etConfirmationCode.getText().toString().trim();

        if (TextUtils.isEmpty(enteredCode)) {
            Toast.makeText(this, "Please enter the confirmation code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredCode.equals(generatedCode)) {
            Toast.makeText(this, "Code verified! You can now reset your password.", Toast.LENGTH_SHORT).show();

            // Enable the new password field and button
            etNewPassword.setVisibility(View.VISIBLE);
            btnResetPassword.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Invalid confirmation code", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update password in Firebase Database
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        userSnapshot.getRef().child("password").setValue(newPassword);
                        Toast.makeText(ForgotPassword.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(ForgotPassword.this, "Email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPassword.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
