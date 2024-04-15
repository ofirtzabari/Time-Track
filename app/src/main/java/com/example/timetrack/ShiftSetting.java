package com.example.timetrack;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class ShiftSetting extends AppCompatActivity {

    Button save;
    EditText startDate;
    TimePicker startTime, endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_setting);

        startDate = findViewById(R.id.editTextDate1);
        save = findViewById(R.id.button1);

        startTime = findViewById(R.id.timePicker1);
        endTime = findViewById(R.id.timePicker2);

        startDate.setOnClickListener(v -> {

            // Get the current date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(ShiftSetting.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Display Selected date in EditText
                        startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        //email from shared preferences
        SharedPreferences sp= this.getSharedPreferences("Login", MODE_PRIVATE);
        String email = sp.getString("user", "");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the date is selected
                if(startDate.getText().toString().isEmpty()){
                    Toast.makeText(ShiftSetting.this, "Select a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the date string from the EditText
                String dateString = startDate.getText().toString();

                // Convert the date string to the format "yyyy-MM-dd"
//                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                String formattedDateString = LocalDate.parse(dateString, inputFormatter).format(outputFormatter);

                // Get the start and end times from the TimePickers
                int startHour = startTime.getHour();
                int startMinute = startTime.getMinute();
                int endHour = endTime.getHour();
                int endMinute = endTime.getMinute();

                //check if the start time is before the end time
                if(startHour > endHour || (startHour == endHour && startMinute >= endMinute)){
                    Toast.makeText(ShiftSetting.this, "Start time must be before end time", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get year month and day from the date string
                String[] dateParts = dateString.split("-");
                int year = Integer.parseInt(dateParts[2]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[0]);

                // create LocalDateTime objects for the start and end times
                LocalDateTime startDateTime =  LocalDate.of(year, month, day).atTime(startHour, startMinute);
                LocalDateTime endDateTime =  LocalDate.of(year, month, day).atTime(endHour, endMinute);


                // Create a new shift
                Shift shift = new Shift(startDateTime, endDateTime, email);
                shift.StoreShiftInDB();
                //return to the main activity
                Intent intent = new Intent(ShiftSetting.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //check if have Shift id in putExtra in the intent
        if(getIntent().hasExtra("shiftId")){
            //get the shift id from the intent
            String shiftId = getIntent().getStringExtra("shiftId");

            //initialize the clocks and date with the shift data
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //get the shift data from the database
            DocumentReference docRef = db.collection("shifts").document(shiftId);
            docRef.get(Source.CACHE).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            ///get the data from the document
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });


        }
    }
}