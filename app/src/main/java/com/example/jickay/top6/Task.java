package com.example.jickay.top6;

/**
 * Created by ViJack on 6/20/2017.
 */

public class Task {

    private String title;
    private String date;
    private String description;
    private int importance;
    private int urgency;

    private boolean completion;

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
    public int getUrgency() { return urgency; }
    public boolean getCompletion() { return completion; }

    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setDescription(String desc) { this.description = desc; }
    public void setImportance (int importance) { this.importance = importance; }
    public void setUrgency (int urgency) { this.urgency = urgency; }
    public void setCompletion(boolean completion) { this.completion = completion; }
}
