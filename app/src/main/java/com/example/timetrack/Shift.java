package com.example.timetrack;

import android.annotation.SuppressLint;

import java.time.Duration;
import java.time.LocalDateTime;

import androidx.annotation.NonNull;

public class Shift {
    LocalDateTime startTime;
    LocalDateTime endTime;

    public Shift(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public float calc_time() {
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMinutes() / 60.0f;
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%f", calc_time());
    }
}
