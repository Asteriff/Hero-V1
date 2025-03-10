package com.example.heromodelapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;


public class ChatBotActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        messageList = new ArrayList<>();

        //bottom nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_chatbot);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_chatbot) {
                return true;
            } else if (itemId == R.id.bottom_reminder) {
                startActivity(new Intent(getApplicationContext(), ReminderActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });

        //importing xml values
        recyclerView = findViewById(R.id.recycler_view);
        sendButton = findViewById(R.id.send_btn);
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        messageEditText = findViewById(R.id.message_edit_text);

        //send button logic
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pasting user input into right chat box
                String message = messageEditText.getText().toString().trim();
                messageEditText.setText("");
                addToChat(message, Message.SENT_BY_ME);

                //basic scripted queried responses
                if (message.equalsIgnoreCase("hello")) {
                    addResponse("Hello! How can I assist you today?");
                }
                if (message.equalsIgnoreCase("what can you do")) {
                    addResponse("I will try to classify your symptoms as best as I can.");
                }
                if (message.equalsIgnoreCase("bye")) {
                    addResponse("Goodbye! Have a great day!");
                }
            }
        });
    }

    //adding user messages to the recycler view
    private void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    //adding bot responses to the recycler view
    void addResponse(String response) {
        addToChat(response, Message.SENT_BY_BOT);
    }


}
