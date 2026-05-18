package com.sit708.learningassistant.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Stores each completed quiz attempt for the History screen.
 * Added in Task 10.1.
 */
@Entity(tableName = "quiz_attempts")
public class QuizAttempt {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;        // owner
    public String topic;
    public int totalQuestions;
    public int correctAnswers;
    public long timestamp;         // System.currentTimeMillis()

    // JSON-serialised list of question texts (for History detail view)
    public String questionsJson;   // e.g. ["Q1 text","Q2 text",...]
    // JSON-serialised selected answers and correct answers
    public String selectedAnswersJson;  // e.g. ["A","B",...]
    public String correctAnswersJson;   // e.g. ["A","C",...]

    public QuizAttempt() {}

    @androidx.room.Ignore
    public QuizAttempt(String username, String topic,
                       int totalQuestions, int correctAnswers,
                       long timestamp,
                       String questionsJson,
                       String selectedAnswersJson,
                       String correctAnswersJson) {
        this.username = username;
        this.topic = topic;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.timestamp = timestamp;
        this.questionsJson = questionsJson;
        this.selectedAnswersJson = selectedAnswersJson;
        this.correctAnswersJson = correctAnswersJson;
    }

    public int getIncorrectAnswers() {
        return totalQuestions - correctAnswers;
    }
}
