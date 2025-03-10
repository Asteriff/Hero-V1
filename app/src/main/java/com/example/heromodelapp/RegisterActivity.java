package com.example.heromodelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button join_now, log_in;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //importing xml values
        firstNameEditText = findViewById(R.id.first);
        lastNameEditText = findViewById(R.id.last);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm);
        join_now = findViewById(R.id.join_now);

        //initiate firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //initiate firebase auth
        mAuth = FirebaseAuth.getInstance();

        //redirect to log in activity if user has account
        log_in = findViewById(R.id.log_in);
        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        //registration logic
        Button joinNowButton = findViewById(R.id.join_now);
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collect user input
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                //verify matching passwords
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                //collect information and store into firebase authenticator
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //if sign in is valid retrieve the current users details for UI
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    //after using email and password store the other information into firestore db
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    //mapping user details to collection in firestore document
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("firstName", firstName);
                                    userData.put("lastName", lastName);
                                    userData.put("email", email);
                                    //finish registration with a Toast for success and failure of registration
                                    db.collection("users").document(user.getUid()).set(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                        //if success start app Main
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Failed to register user data", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    //fall back error for a failed authentication
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}