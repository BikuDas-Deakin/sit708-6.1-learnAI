package com.sit708.learningassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sit708.learningassistant.R;
import com.sit708.learningassistant.adapters.TaskAdapter;
import com.sit708.learningassistant.models.LearningTask;
import com.sit708.learningassistant.utils.DummyDataProvider;
import com.sit708.learningassistant.utils.SessionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        TextView tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText(session.getUsername());

        List<LearningTask> tasks = DummyDataProvider.getTasks();
        long dueCount = tasks.stream().filter(LearningTask::isDue).count();
        TextView tvTaskDue = findViewById(R.id.tvTaskDue);
        tvTaskDue.setText("You have " + dueCount + " task" + (dueCount == 1 ? "" : "s") + " due");

        RecyclerView rv = findViewById(R.id.rvTasks);
        rv.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter adapter = new TaskAdapter(tasks, this);
        rv.setAdapter(adapter);

        // Logout
        TextView tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> {
            session.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    public void onStartTask(LearningTask task) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("task_title", task.getTitle());
        intent.putExtra("task_desc", task.getDescription());
        intent.putExtra("task_topic", task.getTopic());
        List<LearningTask> tasks = DummyDataProvider.getTasks();
        int index = tasks.indexOf(task);
        intent.putExtra("task_index", index);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
