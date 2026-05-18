package com.sit708.learningassistant.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AttemptDao {

    @Insert
    void insert(QuizAttempt attempt);

    /** All attempts for a user, newest first. */
    @Query("SELECT * FROM quiz_attempts WHERE username = :username ORDER BY timestamp DESC")
    List<QuizAttempt> getAttemptsForUser(String username);

    /** Aggregate stats for a user. */
    @Query("SELECT SUM(totalQuestions) FROM quiz_attempts WHERE username = :username")
    int getTotalQuestions(String username);

    @Query("SELECT SUM(correctAnswers) FROM quiz_attempts WHERE username = :username")
    int getTotalCorrect(String username);

    @Query("SELECT COUNT(*) FROM quiz_attempts WHERE username = :username")
    int getAttemptCount(String username);
}
