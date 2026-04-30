package com.sit708.learningassistant.utils;

import com.sit708.learningassistant.models.LearningTask;
import com.sit708.learningassistant.models.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DummyDataProvider {

    public static List<LearningTask> getTasks() {
        List<LearningTask> tasks = new ArrayList<>();

        // Task 1 - Algorithms
        List<Question> q1 = new ArrayList<>();
        q1.add(new Question(
                "What is the time complexity of binary search?",
                Arrays.asList("O(n)", "O(log n)", "O(n²)", "O(1)"),
                1
        ));
        q1.add(new Question(
                "Which sorting algorithm has the best average-case performance?",
                Arrays.asList("Bubble Sort", "Selection Sort", "Merge Sort", "Insertion Sort"),
                2
        ));
        q1.add(new Question(
                "A stack follows which principle?",
                Arrays.asList("FIFO", "LIFO", "Random", "Priority"),
                1
        ));
        tasks.add(new LearningTask(
                "Generated Task 1",
                "Introduction to Algorithm Complexity and Data Structures",
                "Algorithms",
                q1,
                true
        ));

        // Task 2 - Data Structures
        List<Question> q2 = new ArrayList<>();
        q2.add(new Question(
                "Which data structure uses nodes and pointers?",
                Arrays.asList("Array", "Linked List", "Stack", "Queue"),
                1
        ));
        q2.add(new Question(
                "What is the worst-case time for accessing an element in a hash table?",
                Arrays.asList("O(1)", "O(log n)", "O(n)", "O(n²)"),
                2
        ));
        q2.add(new Question(
                "A binary tree has at most how many children per node?",
                Arrays.asList("1", "2", "3", "Unlimited"),
                1
        ));
        tasks.add(new LearningTask(
                "Generated Task 2",
                "Core Data Structures: Linked Lists, Trees and Hash Tables",
                "Data Structures",
                q2,
                false
        ));

        // Task 3 - Web Development
        List<Question> q3 = new ArrayList<>();
        q3.add(new Question(
                "Which HTML tag is used to define an internal style sheet?",
                Arrays.asList("<css>", "<style>", "<script>", "<link>"),
                1
        ));
        q3.add(new Question(
                "What does CSS stand for?",
                Arrays.asList("Computer Style Sheets", "Creative Style Sheets", "Cascading Style Sheets", "Colorful Style Sheets"),
                2
        ));
        q3.add(new Question(
                "Which HTTP method is used to submit form data?",
                Arrays.asList("GET", "POST", "PUT", "DELETE"),
                1
        ));
        tasks.add(new LearningTask(
                "Generated Task 3",
                "Web Development Fundamentals: HTML, CSS and HTTP",
                "Web Development",
                q3,
                false
        ));

        // Task 4 - Testing
        List<Question> q4 = new ArrayList<>();
        q4.add(new Question(
                "What type of testing validates individual units of code?",
                Arrays.asList("Integration Testing", "Unit Testing", "System Testing", "Acceptance Testing"),
                1
        ));
        q4.add(new Question(
                "Which testing approach tests without knowledge of internal code?",
                Arrays.asList("White-box testing", "Grey-box testing", "Black-box testing", "Clear-box testing"),
                2
        ));
        q4.add(new Question(
                "TDD stands for?",
                Arrays.asList("Test-Driven Design", "Test-Driven Development", "Test During Deployment", "Technical Design Document"),
                1
        ));
        tasks.add(new LearningTask(
                "Generated Task 4",
                "Software Testing Strategies and Best Practices",
                "Testing",
                q4,
                false
        ));

        return tasks;
    }

    public static List<String> getTopics() {
        return Arrays.asList(
                "Algorithms", "Data Structures", "Web Development", "Testing",
                "Databases", "Machine Learning", "Android Dev", "Networking",
                "Operating Systems", "Security", "Cloud Computing", "DevOps",
                "Python", "Java", "System Design"
        );
    }
}
