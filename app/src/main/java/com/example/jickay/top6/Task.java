package com.example.jickay.top6;

/**
 * Created by ViJack on 6/20/2017.
 */

public class Task {

    private String title;
    private String date;
    private String description;

    public Task(String newTitle, String newDate, String newDescription) {
        title = newTitle;
        date = newDate;
        description = newDescription;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
}
