package com.example.timetrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JobSetting extends AppCompatActivity {

    Button save;
    EditText jobName, hourlyRate, startDate;

    // Initialize Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_setting);

        save = findViewById(R.id.button);
        jobName = findViewById(R.id.editTextText);
        hourlyRate = findViewById(R.id.editTextText12);
        startDate = findViewById(R.id.editTextDate);

        SharedPreferences sp= this.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();

        startDate.setOnClickListener(v -> {

            // Get the current date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(JobSetting.this,R.style.DatePickerDialogTheme,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Display Selected date in EditText
                        startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        save.setOnClickListener(v -> {
            if (jobName.getText().toString().isEmpty() || hourlyRate.getText().toString().isEmpty() || startDate.getText().toString().isEmpty()) {
                Toast.makeText(JobSetting.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(JobSetting.this, "0", Toast.LENGTH_SHORT).show();

                // Save to Firestore
                Map<String, Object> job = new HashMap<>();
                //check if the fields are logically correct like salary is a number not negative and the date is logical
                String hourlyRateString = hourlyRate.getText().toString();
                double hourlyRateDouble = Double.parseDouble(hourlyRateString);
                if (hourlyRateDouble < 0) {
                    Toast.makeText(JobSetting.this, "Hourly rate cannot be negative", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(JobSetting.this, "1", Toast.LENGTH_SHORT).show();
                String date = startDate.getText().toString();
                String[] dateParts = date.split("-");

                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                if (year < 0|| month < 1 || month > 12 || day < 1 || day > 31) {
                    Toast.makeText(JobSetting.this, "Invalid date", Toast.LENGTH_SHORT).show();
                    return;
                }

                job.put("jobName", jobName.getText().toString());
                job.put("hourlyRate", hourlyRate.getText().toString());
                job.put("startDate", startDate.getText().toString());


                String email = sp.getString("user", "");
                db.collection("jobs").document(email).set(job).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(JobSetting.this, "Job saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(JobSetting.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JobSetting.this, "Error saving job", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}