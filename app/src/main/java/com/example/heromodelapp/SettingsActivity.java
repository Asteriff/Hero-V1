package com.example.heromodelapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class SettingsActivity extends AppCompatActivity {

    private TextView emailTextView, passwordTextView;
    private FirebaseAuth mAuth;
    private ListenerRegistration userListener;
    private FirebaseFirestore db;
    private TextView accountNameTextView;
    private Switch darkMode;
    boolean isNightOn;

    private Switch notificationSwitch;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //dark_mode switch logic
        Switch darkMode = findViewById(R.id.dark_switch);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            isNightOn = false;
        }else {
            isNightOn = true;
        }

        //click switch for night mode
        darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (isNightOn){
                     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                     isNightOn = false;
                 } else {
                     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                     isNightOn = true;
                 }
            }
        });


        //initiate firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //retrieve xml values
        emailTextView = findViewById(R.id.populate_email);
        passwordTextView = findViewById(R.id.populate_password);
        accountNameTextView = findViewById(R.id.account_name);

        //collect current user information
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userListener = db.collection("users").document(userId)
                    .addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
                        @Override
                        //if user info cannot be retrieve show error message
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(SettingsActivity.this, "Error: no user found", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //retrieved user information
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                //collect current user fire and last name
                                String firstName = documentSnapshot.getString("firstName");
                                String lastName = documentSnapshot.getString("lastName");

                                // have first name + last name in account name text
                                String fullName = firstName + " " + lastName;
                                accountNameTextView.setText(fullName);
                            }
                        }
                    });
        }

        //apply email from current user to text view
        if (currentUser != null) {
            String email = currentUser.getEmail();
            emailTextView.setText(email);
        }

        //dummy text for representation
        passwordTextView.setText("**********");

        //use firebase auth sign out method to log out and start the first activity
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(SettingsActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, StartUpActivity.class));
                finish();
            }
        });

        //bottom nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_settings);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_chatbot) {
                startActivity(new Intent(getApplicationContext(), ChatBotActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_reminder) {
                startActivity(new Intent(getApplicationContext(), ReminderActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            }
            return false;
        });


        //enable or disable notifications and provide proof for user with Toast
        notificationSwitch = findViewById(R.id.notification_switch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disableNotifications();
                    Toast.makeText(SettingsActivity.this, "Notifications disabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Notifications enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //display terms and conditions dialog
        Button readButton = findViewById(R.id.tanc_read);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsAndConditionsDialog();
            }
        });
    }

    //notification manager allows us to turn off device notifications for the app
    private void disableNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    //showing the dialog with generated t and c with a back button for easier exit
    private void showTermsAndConditionsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_tanc);

        Button backButton = dialog.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }
}
