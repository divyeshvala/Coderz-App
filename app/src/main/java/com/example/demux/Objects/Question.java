package com.example.demux.Objects;

/**
 * It is an object which contains all the details about the question.
 */
public class Question
{
    String title;
    String descriptionHTML;
    String topics;
    String tags;
    int difficultyLevel;
    int frequency;
    String questionId;

    /**
     * It returns the Question object containing following properties.
     *  @param title     title of the question
     * @param descriptionHTML      description of the question as HTML
     * @param topics     string containing coma separated topics related to question
     * @param tags       string containing coma separated tags related to question
     * @param difficultyLevel     difficulty level of question : [ 1-easy, 2-medium, 3-hard ]
     * @param frequency      number of times the question has been asked
     * @param questionId    unique id of the question
     */

    public Question(String title, String descriptionHTML, String topics, String tags, int difficultyLevel, int frequency, String questionId) {
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

    public String getQuestionId() {
        return questionId;
    }
}
