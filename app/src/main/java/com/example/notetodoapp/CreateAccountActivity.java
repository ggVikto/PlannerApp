package com.example.notetodoapp;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    EditText editText_email_register, editText_password_register, editText_confirm_password_register;
    Button create_account_btn;
    TextView login_text_view_btn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        editText_email_register = findViewById(R.id.editText_email_register);
        editText_password_register = findViewById(R.id.editText_password_register);
        editText_confirm_password_register = findViewById(R.id.editText_confirm_password_register);
        create_account_btn = findViewById(R.id.login_btn);
        login_text_view_btn = findViewById(R.id.register_text_view_btn);
        progressBar = findViewById(R.id.progressBar);

        create_account_btn.setOnClickListener(v-> createAccount());
        login_text_view_btn.setOnClickListener(v-> finish());
    }

    void createAccount() {
        String email = editText_email_register.getText().toString();
        String password = editText_password_register.getText().toString();
        String confirmPassword = editText_confirm_password_register.getText().toString();
        boolean isValidated = validateData(email, password, confirmPassword);
        if(!isValidated) return;
        createAccountInFirebase(email, password);
    }

    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            HashMap<String, String> userInfo = new HashMap<>();
                            userInfo.put("email", editText_email_register.getText().toString());
                            //userInfo.put("notes", "");
                            userInfo.put("profileImage", "");
                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInfo);
                            Utility.showToast(CreateAccountActivity.this, "Вы успешно зарегестрировались!");
                            firebaseAuth.signOut();
                            finish();
                        }
                        else{
                            Utility.showToast(CreateAccountActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                        }
                    }
                }
        );
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            create_account_btn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            create_account_btn.setVisibility(View.VISIBLE);
        }
    }


    boolean validateData(String email, String password, String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editText_email_register.setError("Неверный формат email");
            return false;
        }
        if(password.length()<6){
            editText_password_register.setError("Длина пароля должна быть не менее 6 символов");
            return false;
        }
        if(!password.equals(confirmPassword)){
            editText_confirm_password_register.setError("Пароли не совпадают");
            return false;
        }
        return true;
    }

}