package com.sit708.learningassistant.models;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;
    private int selectedAnswerIndex = -1;

    public Question(String questionText, List<String> options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public int getSelectedAnswerIndex() { return selectedAnswerIndex; }
    public void setSelectedAnswerIndex(int index) { this.selectedAnswerIndex = index; }

    public boolean isCorrect() {
        return selectedAnswerIndex == correctAnswerIndex;
    }

    public String getCorrectAnswerText() {
        if (correctAnswerIndex >= 0 && correctAnswerIndex < options.size()) {
            return options.get(correctAnswerIndex);
        }
        return "";
    }

    public String getSelectedAnswerText() {
        if (selectedAnswerIndex >= 0 && selectedAnswerIndex < options.size()) {
            return options.get(selectedAnswerIndex);
        }
        return "No answer selected";
    }
}
