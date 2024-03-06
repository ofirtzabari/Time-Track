package com.example.timetrack;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.K;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //test
        // Create a new user with a first, middle, and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "OFIR");
//        user.put("middle", "T");
//        user.put("last", "12");
//        user.put("born", 1912);
//
//        // Add a new document with a generated ID
//        db.collection("users")
//                .add(user);
        /// end of test
//        LocalDateTime startTime = LocalDateTime.of(2024, 2, 28, 10, 30, 0);
//        LocalDateTime endTime = LocalDateTime.of(2024, 2, 28, 15, 45, 30);
//        Shift s1 = new Shift(startTime, endTime);
////        Map<String, Object> shift1 = new HashMap<>();
////        shift1.put("start", s1.startTime);
////        shift1.put("end", s1.endTime);
//        List<Map<String, Object>> arrayOfMaps = new ArrayList<>();
//
//        arrayOfMaps.add(ObjectToMapConverter.convertObjectToMap(s1));
//
//
//        db.collection("users")
//                .document("OFEK@gmail.com").set(arrayOfMaps);


        User u = new User("Shalev", "Shalev@gmail.com", 100f, "1234");
        u.StoreUserInDB();
        User u1 = new User("Ofir", "Ofir@gmail.com", 100f, "1234");
        u1.StoreUserInDB();


        LocalDateTime startTime = LocalDateTime.of(2024, 2, 28, 10, 30, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 2, 28, 15, 45, 30);
        Shift s1 = new Shift(startTime, endTime, u);
        Shift s2 = new Shift(startTime, endTime, u);
        Shift s3 = new Shift(startTime, endTime, u1);
        s1.StoreUserInDB();
        s2.StoreUserInDB();
        s3.StoreUserInDB();



    }
}