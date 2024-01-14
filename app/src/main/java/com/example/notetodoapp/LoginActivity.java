package com.example.notetodoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText editText_email_register, editText_password_register;
    Button login_btn;
    TextView register_text_view_btn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_email_register = findViewById(R.id.editText_email_register);
        editText_password_register = findViewById(R.id.editText_password_register);
        login_btn = findViewById(R.id.login_btn);
        register_text_view_btn = findViewById(R.id.register_text_view_btn);
        progressBar = findViewById(R.id.progressBar);

        login_btn.setOnClickListener(v-> loginUser());
        register_text_view_btn.setOnClickListener(v-> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));
    }

    void loginUser() {
        String email = editText_email_register.getText().toString();
        String password = editText_password_register.getText().toString();
        boolean isValidated = validateData(email, password);
        if(!isValidated) return;
        loginAccountInFirebase(email, password);
    }

    void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            changeInProgress(false);
            if(task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
            }else{
                Utility.showToast(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
            }
        });
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
        }
    }


    boolean validateData(String email, String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editText_email_register.setError("Неверный формат email");
            return false;
        }
        if(password.length()<6){
            editText_password_register.setError("Неверный пароль");
            return false;
        }
        return true;
    }
}