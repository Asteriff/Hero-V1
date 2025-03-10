package com.example.heromodelapp;

public class Message {

    //setters and getters for messages sent by user and bot
    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT = "bot";

    String message;
    String sentBy;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    //declaring global variables
    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }
}