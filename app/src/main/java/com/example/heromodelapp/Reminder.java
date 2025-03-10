package com.example.heromodelapp;
import java.util.Calendar;

public class Reminder {
    private String title;
    private String description;
    private long timeInMillis;

    //direct reminder structure
    public Reminder(String title, String description, int year, int month, int day, int hour, int minute) {
        this.title = title;
        this.description = description;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        this.timeInMillis = calendar.getTimeInMillis();
    }

    //getters and setters for reminders
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }
}
