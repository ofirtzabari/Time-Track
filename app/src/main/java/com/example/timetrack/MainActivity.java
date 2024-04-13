package com.example.timetrack;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
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
    TextView jobNameView ;
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

        plus = findViewById(R.id.plusBtn);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShiftSetting.class);
                startActivity(intent);
                finish();
            }
        });

        // set the job name in the text view

        DocumentReference docRef = db.collection("jobs").document(email);
        //get jobName field from the document
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(documentSnapshot.exists()){
//                    String data = documentSnapshot.getString("jobName");
//                    jobNameView.setText(data);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: " + e.toString());
//            }
//        });


        //Toast.makeText(MainActivity.this, docRef.get().toString(), Toast.LENGTH_LONG).show();


        //DocumentReference docRef = db.collection("jobs").document(email);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String, Object> data = document.getData();
                        //Toast.makeText(MainActivity.this, data.get("jobName").toString(), Toast.LENGTH_LONG).show();
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
                        shifts.add(new Shift(start, end, email));
                    }

                    LinearLayout liniarLayout = new LinearLayout(MainActivity.this);
                    liniarLayout.setOrientation(LinearLayout.VERTICAL);
                    GradientDrawable border = new GradientDrawable();
                    border.setColor(Color.WHITE); // Set background color -> white
                    border.setStroke(2, Color.RED); // Set border color and width
                    border.setCornerRadius(8); // Set corner radius

                    for (Shift s : shifts){
                        TextView textView = new TextView(MainActivity.this);
                        textView.setTextSize(20);
                        textView.setPadding(10, 10, 0, 10);

                        textView.setBackground(border);
                        textView.setText(s.toString());
                        liniarLayout.addView(textView);
                    }
                    DecimalFormat df = new DecimalFormat("#.##");
                    // Format the totalHours value with the DecimalFormat object
                    String formattedTotalHours = df.format(totalHours);
                    scrollView.addView(liniarLayout);
                    totalHoursView.setText("Total Hours: " + formattedTotalHours);
                }
            }
        });





    }
}