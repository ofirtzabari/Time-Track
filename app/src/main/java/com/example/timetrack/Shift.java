package com.example.timetrack;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.FirebaseFirestore;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;


public class Shift {
    LocalDateTime startTime;
    LocalDateTime endTime;

    String email;
    public Shift(LocalDateTime startTime, LocalDateTime endTime, String email) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.email = email;
    }

    public float calc_time() {
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMinutes() / 60.0f;
    }

    public void StoreUserInDB(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> shift = new HashMap<>();

        shift.put("startTime", startTime);
        shift.put("endTime", endTime);
        shift.put("totalTime", calc_time());
        shift.put("email", email);

        db.collection("shifts").add(shift);
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%f", calc_time());
    }
}
