package com.sit708.learningassistant.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuizResponse {

    @SerializedName("quiz")
    private List<QuizQuestion> quiz;

    public List<QuizQuestion> getQuiz() { return quiz; }

    public static class QuizQuestion {
        @SerializedName("question")
        private String question;

        @SerializedName("options")
        private List<String> options;

        @SerializedName("correct_answer")
        private String correctAnswer; // "A", "B", "C", or "D"

        public String getQuestion() { return question; }
        public List<String> getOptions() { return options; }
        public String getCorrectAnswer() { return correctAnswer; }

        /**
         * Converts letter answer (A/B/C/D) to zero-based index.
         */
        public int getCorrectAnswerIndex() {
            if (correctAnswer == null || correctAnswer.isEmpty()) return 0;
            switch (correctAnswer.trim().toUpperCase()) {
                case "A": return 0;
                case "B": return 1;
                case "C": return 2;
                case "D": return 3;
                default: return 0;
            }
        }

        /**
         * Convert to the app's Question model.
         */
        public Question toQuestion() {
            return new Question(question, options, getCorrectAnswerIndex());
        }
    }
}
