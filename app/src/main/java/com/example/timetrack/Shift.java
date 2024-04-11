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
        // create a string representation of the shift
        return String.format("Start: %s\nEnd: %s\nTotal Time: %.2f", startTime, endTime, calc_time());

    }

    //crete a new shift object from a map
    public static LocalDateTime fromMap(Map<String, Object> data){

        Long year = (Long) data.get("year");
        Long month = (Long) data.get("monthValue");
        Long day = (Long) data.get("dayOfMonth");
        Long hour = (Long) data.get("hour");
        Long minute = (Long) data.get("minute");
        LocalDateTime l = LocalDateTime.of(year.intValue(), month.intValue(), day.intValue(),hour.intValue(),minute.intValue());
        return LocalDateTime.of(year.intValue(), month.intValue(), day.intValue(),hour.intValue(),minute.intValue());
    }
}
