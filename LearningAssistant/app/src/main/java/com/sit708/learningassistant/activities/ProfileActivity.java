package com.sit708.learningassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.sit708.learningassistant.R;
import com.sit708.learningassistant.models.AppDatabase;
import com.sit708.learningassistant.network.GeminiService;
import com.sit708.learningassistant.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Task 10.1 – New screen.
 * Displays user profile stats loaded from quiz_attempts,
 * an AI-generated performance summary (Gemini), Share and Upgrade buttons.
 */
public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvTier;
    private TextView tvTotalQ, tvCorrectQ, tvIncorrectQ, tvAiSummary;
    private ProgressBar pbSummary;
    private MaterialButton btnShare, btnHistory, btnUpgrade, btnAiSummary;

    private SessionManager session;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private int totalQ = 0, totalCorrect = 0, totalIncorrect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        session = new SessionManager(this);
        db = AppDatabase.getInstance(this);

        tvUsername    = findViewById(R.id.tvUsername);
        tvEmail       = findViewById(R.id.tvEmail);
        tvTier        = findViewById(R.id.tvTier);
        tvTotalQ      = findViewById(R.id.tvTotalQuestions);
        tvCorrectQ    = findViewById(R.id.tvCorrectAnswers);
        tvIncorrectQ  = findViewById(R.id.tvIncorrectAnswers);
        tvAiSummary   = findViewById(R.id.tvAiSummary);
        pbSummary     = findViewById(R.id.pbAiSummary);
        btnShare      = findViewById(R.id.btnShare);
        btnHistory    = findViewById(R.id.btnHistory);
        btnUpgrade    = findViewById(R.id.btnUpgrade);
        btnAiSummary  = findViewById(R.id.btnGetAiSummary);

        tvUsername.setText(session.getUsername());
        tvEmail.setText(session.getEmail());
        tvTier.setText("Plan: " + session.getTier());

        loadStats();

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        btnUpgrade.setOnClickListener(v -> {
            startActivity(new Intent(this, UpgradeActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        btnShare.setOnClickListener(v -> shareProfile());

        btnAiSummary.setOnClickListener(v -> fetchAiSummary());
    }

    private void loadStats() {
        String username = session.getUsername();
        executor.execute(() -> {
            totalQ        = db.attemptDao().getTotalQuestions(username);
            totalCorrect  = db.attemptDao().getTotalCorrect(username);
            totalIncorrect = totalQ - totalCorrect;
            handler.post(() -> {
                tvTotalQ.setText(String.valueOf(totalQ));
                tvCorrectQ.setText(String.valueOf(totalCorrect));
                tvIncorrectQ.setText(String.valueOf(totalIncorrect));
            });
        });
    }

    private void fetchAiSummary() {
        btnAiSummary.setEnabled(false);
        btnAiSummary.setText("Generating…");
        pbSummary.setVisibility(View.VISIBLE);
        tvAiSummary.setVisibility(View.GONE);

        String prompt = "A student named " + session.getUsername()
                + " has completed quizzes with these stats: "
                + "Total questions attempted: " + totalQ + ", "
                + "Correct answers: " + totalCorrect + ", "
                + "Incorrect answers: " + totalIncorrect + ". "
                + "Write a brief (3-4 sentence) personalised performance summary, "
                + "highlight their strengths, and suggest one area to focus on next.";

        new GeminiService().generateSummary(prompt, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String p, String response) {
                pbSummary.setVisibility(View.GONE);
                tvAiSummary.setText(response);
                tvAiSummary.setVisibility(View.VISIBLE);
                tvAiSummary.setAlpha(0f);
                tvAiSummary.animate().alpha(1f).setDuration(400).start();
                btnAiSummary.setEnabled(true);
                btnAiSummary.setText("🤖 Refresh AI Summary");
            }

            @Override
            public void onFailure(String error) {
                pbSummary.setVisibility(View.GONE);
                btnAiSummary.setEnabled(true);
                btnAiSummary.setText("🤖 Get AI Summary");
                Toast.makeText(ProfileActivity.this,
                        "Could not generate summary: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareProfile() {
        String shareText = "👤 " + session.getUsername() + " | LearnAI Profile\n"
                + "📧 " + session.getEmail() + "\n"
                + "📊 Total Questions: " + totalQ + "\n"
                + "✅ Correct: " + totalCorrect + "\n"
                + "❌ Incorrect: " + totalIncorrect + "\n"
                + "⭐ Plan: " + session.getTier() + "\n"
                + "#LearnAI #SIT708";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My LearnAI Profile");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share Profile via"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh tier in case user just purchased
        tvTier.setText("Plan: " + session.getTier());
        loadStats();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
