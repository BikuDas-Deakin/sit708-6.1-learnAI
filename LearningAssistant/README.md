# LearnAI – LLM-Enhanced Learning Assistant App
### SIT708 Task 6.1D | Deakin University

---

## Overview

**LearnAI** is an Android application that delivers personalised, AI-powered learning experiences for students. It integrates Google's Gemini LLM to provide intelligent tutoring features including contextual hints and answer explanations — directly inside the quiz interface.

---

## Screens

| Screen | Description |
|---|---|
| Splash | Animated welcome screen; routes to Login or Dashboard based on session |
| Login | Username/password authentication backed by Room database |
| Register | Full registration form with validation (username, email, password, phone) |
| Interests | Chip-based topic selector (up to 10 topics) saved to SharedPreferences |
| Dashboard | Personalised greeting, task due badge, AI-generated task cards |
| Quiz | AI-labelled questions with multiple-choice answers and per-question hint button |
| Results | Score summary with AI explanation for every question answer |

---

## LLM-Powered Features

Both utilities use **Google Gemini 1.5 Flash** via REST API. The prompt sent to the model and the response received are both displayed in the UI. Loading and failure states are fully handled.

### 1. Generate Hint for a Question
- **Where:** Quiz screen — tap "💡 Get Hint" on any question card
- **What it does:** Sends the question text and topic to Gemini with a prompt instructing it to provide a helpful hint *without* revealing the answer
- **UI:** Shows a green hint card with the prompt used (monospace) and the AI response below it
- **States:** Loading spinner while waiting; error message on failure

### 2. Explain Why an Answer is Correct/Incorrect
- **Where:** Results screen — loads automatically for every question after submission
- **What it does:** Sends the question, the student's answer, the correct answer, and whether it was right or wrong to Gemini, asking for a clear educational explanation
- **UI:** Shows a cyan explanation card with the prompt used (monospace) and the AI response below it
- **States:** Loading spinner per card; per-card error message on failure

---

## Tech Stack

- **Language:** Java
- **UI:** XML Views, ConstraintLayout, Material Design Components
- **Navigation:** Activity-based with explicit Intents
- **Database:** Room (local user accounts)
- **Session:** SharedPreferences via `SessionManager`
- **LLM:** Google Gemini 1.5 Flash REST API via OkHttp
- **Other:** RecyclerView, CircleImageView, Gson

---

## Project Structure

```
app/src/main/
├── java/com/sit708/learningassistant/
│   ├── activities/
│   │   ├── SplashActivity.java
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   ├── InterestsActivity.java
│   │   ├── MainActivity.java
│   │   ├── QuizActivity.java
│   │   └── ResultsActivity.java
│   ├── adapters/
│   │   ├── TaskAdapter.java
│   │   ├── QuestionAdapter.java
│   │   └── ResultAdapter.java
│   ├── models/
│   │   ├── User.java
│   │   ├── Question.java
│   │   ├── LearningTask.java
│   │   ├── UserDao.java
│   │   └── AppDatabase.java
│   ├── network/
│   │   └── GeminiService.java
│   └── utils/
│       ├── SessionManager.java
│       └── DummyDataProvider.java
└── res/
    ├── layout/         (7 activity layouts + 3 item layouts)
    ├── drawable/       (9 vector/shape drawables)
    └── values/         (strings, colors, themes)
```

---

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/BikuDas-Deakin/SIT708_6.1D.git
   ```
2. Open in **Android Studio** (Hedgehog or later)
3. Add your Gemini API key:
   - Open `app/src/main/java/com/sit708/learningassistant/network/GeminiService.java`
   - Replace `YOUR_GEMINI_API_KEY_HERE` with your key from [Google AI Studio](https://aistudio.google.com/app/apikey)
4. Build and run on an emulator or physical device (API 26+)

> **Note:** The app uses dummy data for tasks and questions. No backend server is required.

---

## Demo

📺 YouTube Demo: *[Link to be added after recording]*

---

## Author

**Bisheshwar Das** | Student ID: S225010182  
Deakin University — SIT708 Mobile Application Development
