package com.example.timetrack;
import android.util.Log;


import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import java.util.Map;


public class User {
    private String name;
    private String email;
    private double hourlyRate;
    private List<Shift> shiftList;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Shift> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    public User(String name, String email, double hourlyRate, String password) {
        this.name = name;
        this.email = email;
        this.hourlyRate = hourlyRate;
        this.shiftList = new ArrayList<>();
        this.password = password;
    }

    public void StoreUserInDB(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();

        user.put("name", this.name);
        user.put("email", this.email);
        user.put("hourlyRate", this.hourlyRate);
        user.put("password", this.password);

        db.collection("users").document(email).set(user);
    }


}
