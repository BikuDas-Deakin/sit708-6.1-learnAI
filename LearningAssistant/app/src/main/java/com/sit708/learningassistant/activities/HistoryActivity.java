package com.sit708.learningassistant.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sit708.learningassistant.R;
import com.sit708.learningassistant.adapters.HistoryAdapter;
import com.sit708.learningassistant.models.AppDatabase;
import com.sit708.learningassistant.models.QuizAttempt;
import com.sit708.learningassistant.utils.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Task 10.1 – New screen.
 * Lists all past quiz attempts for the logged-in user, newest first.
 * Each row is expandable to show per-question detail.
 */
public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmpty;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvHistory = findViewById(R.id.rvHistory);
        tvEmpty   = findViewById(R.id.tvEmpty);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        loadHistory();
    }

    private void loadHistory() {
        String username = new SessionManager(this).getUsername();
        executor.execute(() -> {
            List<QuizAttempt> attempts =
                    AppDatabase.getInstance(this).attemptDao().getAttemptsForUser(username);
            handler.post(() -> {
                if (attempts.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                    rvHistory.setAdapter(new HistoryAdapter(attempts));
                }
            });
        });
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
