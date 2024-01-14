package com.example.notetodoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button_note_main, button_profile_main, button_calendar_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_note_main = findViewById(R.id.button_note_main);
        button_note_main.setOnClickListener(v-> startActivity(new Intent(MainActivity.this, NoteActivity.class)));

        button_profile_main = findViewById(R.id.button_profile_main);
        button_profile_main.setOnClickListener(v-> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        button_calendar_main = findViewById(R.id.button_calendar_main);
        button_calendar_main.setOnClickListener(v-> startActivity(new Intent(MainActivity.this, CalendarActivity.class)));

    }
}