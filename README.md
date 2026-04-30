# LearnAI 🤖
An LLM-Enhanced Learning Assistant Android application built as part of SIT708 Task 6.1D at Deakin University.

## Overview
LearnAI is an Android app that delivers personalised AI-powered learning experiences. Users register, select their learning interests, and receive dynamically generated quizzes powered by Google Gemini 2.5 Flash via a Flask backend. The app features two LLM-powered learning utilities — contextual hints during quizzes and detailed answer explanations on results — with prompts and responses visible in the UI.

## Features

- **Authentication**
  - Login screen with username and password
  - Registration screen with username, email, confirm email, password, confirm password, and phone number
  - Full form validation (email match, password length, duplicate username detection)
  - Session persistence using SharedPreferences
  - Logout button on dashboard clears session and returns to login

- **Interests Setup**
  - Chip-based topic selector after registration
  - Up to 10 topics selectable from 15 options
  - Interests saved to SharedPreferences for personalised task recommendations

- **Dashboard**
  - Personalised greeting with username
  - Task due indicator badge
  - AI-labelled learning task cards generated from selected interests

- **Quiz Screen**
  - Live AI-generated questions fetched from Flask backend (Gemini 2.5 Flash)
  - Loading indicator while backend generates questions
  - Graceful fallback to offline questions if backend is unavailable
  - **LLM Utility 1 — Get Hint:** "💡 Get Hint" button per question; Gemini generates a contextual hint without revealing the answer; prompt and response shown in UI with loading/error states

- **Results Screen**
  - Score summary with correct count and percentage
  - **LLM Utility 2 — Answer Explanation:** Gemini automatically explains why each answer is correct or incorrect; prompt and AI response shown per question card with loading/error states
  - Green badge for correct answers, red for incorrect

- **Navigation**
  - Activity-based navigation with explicit Intents
  - Consistent back navigation throughout all screens
  - Animated transitions between screens

## LLM Integration

### Utility 1 — Generate Hint
Prompt sent to Gemini:
```
You are a helpful tutor. A student is answering a quiz question about [topic].
Give a concise hint (2-3 sentences max) to help them think through the answer
WITHOUT revealing it directly. Question: [question text]
```

### Utility 2 — Explain Answer
Prompt sent to Gemini:
```
You are a helpful tutor explaining quiz results about [topic].
Question: [question]. Student answered: [selected answer] (which is [correct/incorrect]).
Correct answer: [correct answer]. Provide a clear, encouraging explanation (3-4 sentences)
of why the correct answer is right and what the student should understand.
```

Both prompts are displayed in the app UI alongside the AI response so the LLM integration is fully transparent and verifiable.

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java |
| UI | XML Layouts, Material Design Components |
| Navigation | Activity-based navigation |
| Database | Android Room (SQLite) |
| Session | SharedPreferences |
| HTTP Client | Retrofit 2.9 + OkHttp 4.12 |
| LLM Provider | Google Gemini 2.5 Flash |
| Backend | Python Flask 3.1 |
| Min SDK | API 26 |
| Target SDK | API 35 |

## Project Structure

```
app/src/main/java/com/sit708/learningassistant/
├── activities/
│   ├── SplashActivity.java        — Splash screen with auto-navigation
│   ├── LoginActivity.java         — Login with Room credential check
│   ├── RegisterActivity.java      — Registration with full validation
│   ├── InterestsActivity.java     — Chip-based interest selector
│   ├── MainActivity.java          — Dashboard with task cards + logout
│   ├── QuizActivity.java          — AI quiz with hint utility + loading states
│   └── ResultsActivity.java       — Results with AI explanation utility
├── adapters/
│   ├── TaskAdapter.java           — RecyclerView adapter for task cards
│   ├── QuestionAdapter.java       — RecyclerView adapter for quiz questions + hints
│   └── ResultAdapter.java         — RecyclerView adapter for results + explanations
├── models/
│   ├── User.java                  — Room entity for user accounts
│   ├── LearningTask.java          — Model for dashboard task cards
│   ├── Question.java              — Model for quiz questions
│   └── QuizResponse.java          — Retrofit response model for backend API
├── network/
│   ├── GeminiService.java         — OkHttp client for Gemini hint + explanation calls
│   └── BackendApiService.java     — Retrofit client for Flask /getQuiz endpoint
├── database/
│   ├── UserDao.java               — DAO for user insert and login queries
│   └── AppDatabase.java           — Room database singleton
└── utils/
    ├── SessionManager.java        — SharedPreferences login session helper
    └── DummyDataProvider.java     — Offline fallback task and question data
```

## How to Run

### 1. Start the Flask Backend

```bash
git clone https://github.com/sit3057082024/T-6.1D.git
cd T-6.1D
python3 -m venv pyEnv
source pyEnv/bin/activate
pip install -r pip_install_commands.txt
python3 main.py
```

The server runs on `http://localhost:5000`. On macOS, disable AirPlay Receiver in System Settings → General → AirDrop & Handoff if port 5000 is in use.

> **Note:** The tutor's original backend used GradientAI (Llama 3) which is no longer available. The `main.py` was updated to use Google Gemini 2.5 Flash, maintaining the same `/getQuiz` endpoint and JSON response format.

### 2. Add Gemini API Key

Open `app/src/main/java/com/sit708/learningassistant/network/GeminiService.java` and replace:
```java
private static final String API_KEY = "YOUR_GEMINI_API_KEY_HERE";
```
Get a free key at: https://aistudio.google.com/app/apikey

### 3. Open in Android Studio

```bash
git clone https://github.com/BikuDas-Deakin/sit708-6.1-learnAI.git
```

- Open Android Studio → File → Open → select the cloned folder
- Wait for Gradle sync to complete
- Run on an emulator or device with **API 26 or higher**
- Ensure the Flask backend is running before testing quiz generation

## Screenshots

| Login | Register | Interests |
|---|---|---|
|<img width="376" height="805" alt="image" src="https://github.com/user-attachments/assets/97f36b30-1355-4cb6-817d-a567d1fce230" />| <img width="370" height="806" alt="image" src="https://github.com/user-attachments/assets/10e3a3d1-4ab6-4cfd-a0cd-b46b699b35dd" />| <img width="370" height="809" alt="image" src="https://github.com/user-attachments/assets/27482950-c912-468f-896f-cd22f14c9f69" />|

| Dashboard | Quiz + Hint | Results + Explanation |
|---|---|---|
| <img width="374" height="803" alt="image" src="https://github.com/user-attachments/assets/f71180c4-91a3-49d6-b104-89275f581bc3" />| <img width="365" height="684" alt="image" src="https://github.com/user-attachments/assets/95d03799-3619-4faf-a0e0-4c03fe67ac9e" />| <img width="365" height="712" alt="image" src="https://github.com/user-attachments/assets/38fbeaa4-dfab-463f-8e63-358d38af9f6d" />|

## AI Assistance Declaration

This project was completed with supplementary assistance from Claude (Anthropic). The AI tool was used to assist with the following specific areas: Room database entity and DAO design for user authentication; persisting and retrieving selected interests via SharedPreferences; Retrofit GET request setup and JSON array parsing for the Flask /getQuiz endpoint; OkHttp POST calls to the Gemini API for hint and explanation utilities; loading and error state handling for async network calls; passing serialised question data between Activities via Intent extras; RecyclerView alpha fade-in animations; and rewriting the Flask backend to use the Gemini generateContent API in place of the deprecated GradientAI service. All final decisions, testing, and submission were carried out by the student. See the full LLM Declaration Statement in the submission document for annotated prompt details.
## Author

**Bisheshwar Das (Biku)** — S225010182
SIT708 Mobile Application Development, Deakin University, T1 2026
