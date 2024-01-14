package com.example.notetodoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText notes_title_text, notes_content_text;
    ImageButton save_note_btn, delete_btn;
    TextView pageTitleTextView, textView_delete_note;
    String title, content, docId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        notes_title_text = findViewById(R.id.notes_title_text);
        notes_content_text = findViewById(R.id.notes_content_text);
        save_note_btn = findViewById(R.id.menu_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        textView_delete_note = findViewById(R.id.textView_delete_note);
        delete_btn = findViewById(R.id.delete_btn);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId != null && !docId.isEmpty()){
            isEditMode = true;
        }

        notes_title_text.setText(title);
        notes_content_text.setText(content);
        if(isEditMode){
            pageTitleTextView.setText("Редактирование заметки");
            textView_delete_note.setVisibility(View.VISIBLE);
            delete_btn.setVisibility(View.VISIBLE);
        }


        save_note_btn.setOnClickListener((v)-> saveNote());
        textView_delete_note.setOnClickListener((v)-> deleteNoteFromFirebase());
        delete_btn.setOnClickListener((v)-> deleteNoteFromFirebase());
    }

    void deleteNoteFromFirebase(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                .child(currentUser.getUid())
                .child("my_notes")
                .child(docId);

        databaseReference.removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Utility.showToast(NoteDetailsActivity.this, "Заметка успешно удалена");
                finish();
            } else {
                Utility.showToast(NoteDetailsActivity.this, "Ошибка при удалении заметки");
            }
        });
    }


    void saveNote() {
        String noteTitle = notes_title_text.getText().toString();
        String noteContent = notes_content_text.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            notes_title_text.setError("Заголовок не может быть пустым");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        saveNoteToFirebase(note);
    }

    void updateNoteInFirebase(String docId, Note note){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                .child(currentUser.getUid())
                .child("my_notes")
                .child(docId);

        databaseReference.setValue(note).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Utility.showToast(NoteDetailsActivity.this, "Заметка успешно обновлена");
                finish();
            } else {
                Utility.showToast(NoteDetailsActivity.this, "Ошибка при обновлении заметки");
            }
        });
    }

    void saveNoteToFirebase(Note note){
        if(isEditMode){
            updateNoteInFirebase(docId, note);
        }else {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser != null;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                    .child(currentUser.getUid())
                    .child("my_notes")
                    .push();
            databaseReference.setValue(note).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetailsActivity.this, "Заметка успешно добавлена");
                    finish();
                } else {
                    Utility.showToast(NoteDetailsActivity.this, "Заметка не была добавлена");
                }
            });
        }
    }
}