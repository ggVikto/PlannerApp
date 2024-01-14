package com.example.notetodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.Query;

public class NoteActivity extends AppCompatActivity {

    FloatingActionButton add_note_btn;
    RecyclerView recycler_view;
    NoteAdapter noteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        add_note_btn = findViewById(R.id.add_note_btn);
        recycler_view = findViewById(R.id.recycler_view);

        add_note_btn.setOnClickListener(v-> startActivity(new Intent(NoteActivity.this, NoteDetailsActivity.class)));
        setUpRecyclerView();
    }

    void setUpRecyclerView() {
        Query query = Utility.getDatabaseReferenceForNotes();
        FirebaseRecyclerOptions<Note> options = new FirebaseRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recycler_view.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}