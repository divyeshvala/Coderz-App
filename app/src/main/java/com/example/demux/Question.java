package com.example.demux;

public class Question
{
    String title;
    String description;
    String tags;
    int frequency;

    public Question(String title, String description, String tags, int frequency) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.frequency = frequency;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public int getFrequency() {
        return frequency;
    }
}
