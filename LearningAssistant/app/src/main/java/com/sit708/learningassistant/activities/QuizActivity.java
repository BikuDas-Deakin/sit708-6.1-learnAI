package com.sit708.learningassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sit708.learningassistant.R;
import com.sit708.learningassistant.adapters.QuestionAdapter;
import com.sit708.learningassistant.models.LearningTask;
import com.sit708.learningassistant.models.Question;
import com.sit708.learningassistant.models.QuizResponse;
import com.sit708.learningassistant.network.BackendApiService;
import com.sit708.learningassistant.utils.DummyDataProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private List<Question> questions = new ArrayList<>();
    private String topic;
    private RecyclerView rvQuestions;
    private MaterialButton btnSubmit;
    private ProgressBar pbLoading;
    private TextView tvLoadingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        String title = getIntent().getStringExtra("task_title");
        String desc = getIntent().getStringExtra("task_desc");
        topic = getIntent().getStringExtra("task_topic");
        int taskIndex = getIntent().getIntExtra("task_index", 0);

        TextView tvTitle = findViewById(R.id.tvTaskTitle);
        TextView tvDesc = findViewById(R.id.tvTaskDesc);
        tvTitle.setText(title);
        tvDesc.setText(desc);

        rvQuestions = findViewById(R.id.rvQuestions);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));

        btnSubmit = findViewById(R.id.btnSubmit);
        pbLoading = findViewById(R.id.pbLoading);
        tvLoadingStatus = findViewById(R.id.tvLoadingStatus);

        btnSubmit.setVisibility(View.GONE);
        btnSubmit.setOnClickListener(v -> submitQuiz());

        fetchQuestionsFromBackend(topic, taskIndex);
    }

    private void fetchQuestionsFromBackend(String topic, int fallbackIndex) {
        pbLoading.setVisibility(View.VISIBLE);
        tvLoadingStatus.setVisibility(View.VISIBLE);
        tvLoadingStatus.setText("⚡ Generating quiz with AI…");

        BackendApiService.getInstance().getApi()
                .getQuiz(topic)
                .enqueue(new Callback<QuizResponse>() {
                    @Override
                    public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                        pbLoading.setVisibility(View.GONE);
                        tvLoadingStatus.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().getQuiz() != null
                                && !response.body().getQuiz().isEmpty()) {

                            questions = new ArrayList<>();
                            for (QuizResponse.QuizQuestion q : response.body().getQuiz()) {
                                if (q.getOptions() != null && q.getOptions().size() == 4) {
                                    questions.add(q.toQuestion());
                                }
                            }

                            if (questions.isEmpty()) {
                                loadFallbackQuestions(fallbackIndex);
                                showFallbackNotice();
                            } else {
                                showQuestions();
                            }
                        } else {
                            loadFallbackQuestions(fallbackIndex);
                            showFallbackNotice();
                        }
                    }

                    @Override
                    public void onFailure(Call<QuizResponse> call, Throwable t) {
                        pbLoading.setVisibility(View.GONE);
                        tvLoadingStatus.setVisibility(View.GONE);
                        loadFallbackQuestions(fallbackIndex);
                        Toast.makeText(QuizActivity.this,
                                "Backend unavailable — showing offline questions",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadFallbackQuestions(int taskIndex) {
        List<LearningTask> tasks = DummyDataProvider.getTasks();
        int idx = (taskIndex >= 0 && taskIndex < tasks.size()) ? taskIndex : 0;
        questions = new ArrayList<>(tasks.get(idx).getQuestions());
        showQuestions();
    }

    private void showFallbackNotice() {
        Toast.makeText(this, "Showing offline questions (backend returned empty quiz)",
                Toast.LENGTH_SHORT).show();
    }

    private void showQuestions() {
        QuestionAdapter adapter = new QuestionAdapter(questions, topic);
        rvQuestions.setAdapter(adapter);
        btnSubmit.setVisibility(View.VISIBLE);
        btnSubmit.setAlpha(0f);
        btnSubmit.animate().alpha(1f).setDuration(400).start();
    }

    private void submitQuiz() {
        for (Question q : questions) {
            if (q.getSelectedAnswerIndex() == -1) {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("questions", (Serializable) questions);
        intent.putExtra("task_topic", topic);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
