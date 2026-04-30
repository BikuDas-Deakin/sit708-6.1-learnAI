package com.sit708.learningassistant.models;

import java.util.List;

public class LearningTask {
    private String title;
    private String description;
    private String topic;
    private List<Question> questions;
    private boolean isDue;

    public LearningTask(String title, String description, String topic, List<Question> questions, boolean isDue) {
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.questions = questions;
        this.isDue = isDue;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTopic() { return topic; }
    public List<Question> getQuestions() { return questions; }
    public boolean isDue() { return isDue; }
}
