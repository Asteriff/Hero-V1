package com.example.heromodelapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //import xml values
        recyclerView = findViewById(R.id.recyclerViewReminders);
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(reminderList);

        //import recycler view for reminder list
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //show the dialog for adding a new reminder
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddReminderDialog();
            }
        });

        //bottom nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_reminder);
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
                return true;
            } else if (itemId == R.id.bottom_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void showAddReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //inflate the dialog onto the activity
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reminder, null);
        builder.setView(dialogView);

        //import dialog references for various fields
        EditText reminderTitleEditText = dialogView.findViewById(R.id.reminder_title);
        EditText reminderDescriptionEditText = dialogView.findViewById(R.id.reminder_description);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

        //adding reminder if user is happy
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //exporting the user inputted values
                String title = reminderTitleEditText.getText().toString();
                String description = reminderDescriptionEditText.getText().toString();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                //importing values into a new object
                Reminder newReminder = new Reminder(title, description, year, month, day, hour, minute);

                //implement the new object in the recycler view and show a Toast for confirmation
                adapter.addReminder(newReminder);
                Toast.makeText(ReminderActivity.this, "Reminder Added", Toast.LENGTH_SHORT).show();
            }
        });

        //if user changes mind exit the dialog with a cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}