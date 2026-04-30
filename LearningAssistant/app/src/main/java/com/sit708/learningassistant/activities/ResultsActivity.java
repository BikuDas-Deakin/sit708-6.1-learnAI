package com.sit708.learningassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sit708.learningassistant.R;
import com.sit708.learningassistant.adapters.ResultAdapter;
import com.sit708.learningassistant.models.Question;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        @SuppressWarnings("unchecked")
        List<Question> questions = (List<Question>) getIntent().getSerializableExtra("questions");
        String topic = getIntent().getStringExtra("task_topic");

        if (questions == null || questions.isEmpty()) {
            finish();
            return;
        }

        // Calculate score
        int correct = 0;
        for (Question q : questions) {
            if (q.isCorrect()) correct++;
        }
        int total = questions.size();

        TextView tvScore = findViewById(R.id.tvScore);
        tvScore.setText("Score: " + correct + " / " + total
                + "  (" + Math.round((correct * 100f) / total) + "%)");

        RecyclerView rv = findViewById(R.id.rvResults);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ResultAdapter adapter = new ResultAdapter(questions, topic != null ? topic : "");
        rv.setAdapter(adapter);

        MaterialButton btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
