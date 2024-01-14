package com.example.notetodoapp;

public class Event {
    private String id;
    private String content;

    public Event(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    @Override
    public String toString() { return content; } // Used for displaying the Event content in the ListView
}
