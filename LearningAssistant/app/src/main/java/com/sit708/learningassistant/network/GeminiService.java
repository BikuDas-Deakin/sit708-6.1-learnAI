package com.sit708.learningassistant.network;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiService {

    // Replace with your actual Gemini API key
    private static final String API_KEY = "AIzaSyAFlBFzHoiS4msTUXBEx7MfRIu710-W6T8";
    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface GeminiCallback {
        void onSuccess(String prompt, String response);
        void onFailure(String errorMessage);
    }

    /**
     * LLM Utility 1: Generate a hint for a quiz question.
     * Prompt is shown in the UI alongside the response.
     */
    public void generateHint(String questionText, String topic, GeminiCallback callback) {
        String prompt = "You are a helpful tutor. A student is answering a quiz question about "
                + topic + ". Give a concise hint (2-3 sentences max) to help them think through "
                + "the answer WITHOUT revealing it directly.\n\nQuestion: " + questionText;

        callGemini(prompt, callback);
    }

    /**
     * LLM Utility 2: Explain why an answer is correct or incorrect.
     * Prompt is shown in the UI alongside the response.
     */
    public void explainAnswer(String questionText, String selectedAnswer,
                              String correctAnswer, boolean wasCorrect,
                              String topic, GeminiCallback callback) {
        String correctness = wasCorrect ? "correct" : "incorrect";
        String prompt = "You are a helpful tutor explaining quiz results about " + topic + ".\n"
                + "Question: " + questionText + "\n"
                + "Student answered: " + selectedAnswer + " (which is " + correctness + ")\n"
                + "Correct answer: " + correctAnswer + "\n\n"
                + "Provide a clear, encouraging explanation (3-4 sentences) of why the correct "
                + "answer is right and what the student should understand.";

        callGemini(prompt, callback);
    }

    private void callGemini(String prompt, GeminiCallback callback) {
        try {
            JSONObject textPart = new JSONObject().put("text", prompt);
            JSONArray parts = new JSONArray().put(textPart);
            JSONObject content = new JSONObject().put("parts", parts);
            JSONArray contents = new JSONArray().put(content);
            JSONObject body = new JSONObject().put("contents", contents);

            RequestBody requestBody = RequestBody.create(body.toString(), JSON_TYPE);
            Request request = new Request.Builder()
                    .url(BASE_URL + API_KEY)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (!response.isSuccessful()) {
                        mainHandler.post(() -> callback.onFailure("API error " + response.code()
                                + ". Check your API key in GeminiService.java"));
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String text = json
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");
                        mainHandler.post(() -> callback.onSuccess(prompt, text.trim()));
                    } catch (JSONException e) {
                        mainHandler.post(() -> callback.onFailure("Failed to parse response."));
                    }
                }
            });

        } catch (JSONException e) {
            callback.onFailure("Failed to build request: " + e.getMessage());
        }
    }
}
