package com.example.heromodelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;
public class StartUpActivity extends AppCompatActivity {

    private Button login;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        //import xml values
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        //send to register
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartUpActivity.this, RegisterActivity.class));
                finish();
            }
        });

        //send to login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    //if the user has already sign in with an account before it will remember and go straight to main activity
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            startActivity(new Intent(StartUpActivity.this , MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }
}