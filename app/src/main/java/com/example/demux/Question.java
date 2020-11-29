package com.example.demux;

public class Question
{
    String title;
    String descriptionHTML;
    String topics;
    String tags;
    int difficultyLevel;
    int frequency;
    int questionId;

    public Question(String title, String descriptionHTML, String topics, String tags, int difficultyLevel, int frequency, int questionId) {
        this.title = title;
        this.descriptionHTML = descriptionHTML;
        this.topics = topics;
        this.tags = tags;
        this.difficultyLevel = difficultyLevel;
        this.frequency = frequency;
        this.questionId = questionId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescriptionHTML() {
        return descriptionHTML;
    }

    public String getTopics() {
        return topics;
    }

    public String getTags() {
        return tags;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getQuestionId() {
        return questionId;
    }
}
