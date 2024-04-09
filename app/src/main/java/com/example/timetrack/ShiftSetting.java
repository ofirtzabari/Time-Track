package com.example.timetrack;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class ShiftSetting extends AppCompatActivity {

    Button save;
    EditText startDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_setting);


        startDate = findViewById(R.id.editTextDate1);
        save = findViewById(R.id.button1);

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
    }
}