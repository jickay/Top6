package com.example.jickay.top6;

/**
 * Created by ViJack on 6/20/2017.
 */

public class Task {

    private long _id;

    private String title;
    private String date = "Jan 1";
    private String description;
    private int importance = -1;

    private int completion;

    public Task(long id) {
        _id = id;
    }

    public Task(String newTitle, String newDate, String newDescription, int newImportance) {
        title = newTitle;
        date = newDate;
        description = newDescription;
        importance = newImportance;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public int getImportance() { return importance; }
    public int getCompletion() { return completion; }

    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setDescription(String desc) { this.description = desc; }
    public void setImportance (int importance) { this.importance = importance; }
    public void setCompletion(int completion) { this.completion = completion; }
}
