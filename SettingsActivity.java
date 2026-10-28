package com.example.assignment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Simple settings screen. Persists the toggle state using SharedPreferences,
 * which is Android's idiomatic store for small key-value preferences
 * (as opposed to SQLite, which is for structured user data).
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch expirySwitch = findViewById(R.id.switchExpiryAlerts);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        expirySwitch.setChecked(prefs.getBoolean("expiry_alerts", true));

        expirySwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("expiry_alerts", isChecked).apply());
    }
}
