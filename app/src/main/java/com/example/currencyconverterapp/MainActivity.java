package com.example.currencyconverterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    EditText amount;
    Spinner from, to;
    TextView result;
    Button convertBtn;
    Switch themeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amount = findViewById(R.id.amount);
        from = findViewById(R.id.fromCurrency);
        to = findViewById(R.id.toCurrency);
        result = findViewById(R.id.result);
        convertBtn = findViewById(R.id.convertBtn);
        themeSwitch = findViewById(R.id.themeSwitch);

        // Set Spinner Data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.currencies,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        from.setAdapter(adapter);
        to.setAdapter(adapter);

        // Convert Button Click
        convertBtn.setOnClickListener(v -> {
            String amtStr = amount.getText().toString();

            if (amtStr.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amt = Double.parseDouble(amtStr);
            String fromCur = from.getSelectedItem().toString();
            String toCur = to.getSelectedItem().toString();

            double res = convert(amt, fromCur, toCur);

            result.setText("Converted: " + res);
        });

        // Theme Switch
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    // Conversion Logic
    double convert(double amount, String from, String to) {
        double inINR = 0;

        switch (from) {
            case "USD": inINR = amount * 83; break;
            case "EUR": inINR = amount * 90; break;
            case "JPY": inINR = amount * 0.55; break;
            case "INR": inINR = amount; break;
        }

        switch (to) {
            case "USD": return inINR / 83;
            case "EUR": return inINR / 90;
            case "JPY": return inINR / 0.55;
            case "INR": return inINR;
        }

        return 0;
    }
}