package com.example.timetrack;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.K;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    TextView jobNameView, date ;
    TextView totalHoursView ;

    Button plus;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totalHoursView = (TextView)findViewById(R.id.totalHours);
        jobNameView = (TextView)findViewById(R.id.jobNameView);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sp= this.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        String email = sp.getString("user", "");

        scrollView =(ScrollView ) findViewById(R.id.scrollView);
        date = (TextView) findViewById(R.id.date);
        LocalDateTime now = LocalDateTime.now();
        //only date without time is displayed
        date.setText(now.toLocalDate().toString());

        plus = findViewById(R.id.plusBtn);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShiftSetting.class);
                startActivity(intent);
                finish();
            }
        });

        DocumentReference docRef = db.collection("jobs").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String, Object> data = document.getData();
                        //extract job name from the data
                        String jobName = data.get("jobName").toString();

                        jobNameView.setText(jobName);
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, JobSetting.class);
                        startActivity(intent);
                        finish();
                    }

                }

            }
        });


        //bring the shifts from the db of the user and display them in the list view
        //get all shifts from the db and search shifts with the same email in the field "email" and display them in the list view
        db.collection("shifts").get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                if(task.isSuccessful()){
                    double totalHours = 0;
                    List<Shift> shifts = new LinkedList<>();
                    for(DocumentSnapshot document : task.getResult()){
                        Map<String, Object> data = document.getData();

                        LocalDateTime start = Shift.fromMap((Map<String, Object>) data.get("startTime"));
                        LocalDateTime end = Shift.fromMap((Map<String, Object>) data.get("endTime"));
                        totalHours += document.getDouble("totalTime").doubleValue();

                        // add dynamically the shifts to the list view
                        // create a new shift object and add it to the list
                        shifts.add(new Shift(start, end, email, document.getId()));
                    }

                    LinearLayout liniarLayout = new LinearLayout(MainActivity.this);
                    liniarLayout.setOrientation(LinearLayout.VERTICAL);
                    GradientDrawable border = new GradientDrawable();
                    border.setColor(Color.WHITE); // Set background color
                    border.setStroke(2, Color.RED); // Set border color and width
                    border.setCornerRadius(8); // Set corner radius

                    for (final Shift shift : shifts) {
                        // Create a LinearLayout to hold TextView and Button
                        LinearLayout itemLayout = new LinearLayout(MainActivity.this);
                        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

                        // Create a TextView to display the shift information
                        TextView textView = new TextView(MainActivity.this);
                        textView.setTextSize(20);
                        textView.setPadding(10, 10, 0, 10);
                        textView.setBackground(border);
                        textView.setText(shift.toString());

                        // Create a Button to delete the shift
                        Button deleteButton = new Button(MainActivity.this);
                        deleteButton.setText("\uD83D\uDDD1");

                        // Set layout parameters for the delete button
                        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                                150, // Width in pixels
                                150  // Height in pixels
                        );
                        buttonLayoutParams.setMargins(20, 20, 20, 20); // Set left margin
                        deleteButton.setLayoutParams(buttonLayoutParams);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.gravity = Gravity.CENTER;
                        deleteButton.setLayoutParams(params);
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle delete button click event here
                                // For example, remove the shift from the list and update the UI
                                shifts.remove(shift);
                                shift.DeleteShiftFromDB();
                                double totalHours = 0;
                                for(Shift sh: shifts){
                                    totalHours += sh.getTotalHours();
                                }
                                liniarLayout.removeView(itemLayout); // Remove the itemLayout from the parent layout
                                DecimalFormat df = new DecimalFormat("#.##");
                                // Format the totalHours value with the DecimalFormat object
                                String formattedTotalHours = df.format(totalHours);
                                totalHoursView.setText("Total Hours: " + formattedTotalHours);
                            }
                        });

                        //edit button
                        Button editButton = new Button(MainActivity.this);
                        editButton.setText("\uD83D\uDD8A");
                        editButton.setLayoutParams(buttonLayoutParams);
                        editButton.setLayoutParams(params);
                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle edit button click event here
                                // For example, remove the shift from the list and update the UI
                                Intent intent = new Intent(MainActivity.this, ShiftSetting.class);
                                //get the document name in firestore

                                intent.putExtra("shiftId", shift.getId());
                                startActivity(intent);
                                finish();
                            }
                        });

                        // Add TextView and Button to the itemLayout
                        itemLayout.addView(textView);
                        itemLayout.addView(deleteButton);
                        itemLayout.addView(editButton);

                        // Add the itemLayout to the parent layout (liniarLayout)
                        liniarLayout.addView(itemLayout);
                    }
                    scrollView.addView(liniarLayout);

                    DecimalFormat df = new DecimalFormat("#.##");
                    // Format the totalHours value with the DecimalFormat object
                    String formattedTotalHours = df.format(totalHours);
                    totalHoursView.setText("Total Hours: " + formattedTotalHours);

                }
            }
        });





    }
}