package com.sit708.learningassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sit708.learningassistant.R;
import com.sit708.learningassistant.models.AppDatabase;
import com.sit708.learningassistant.models.User;
import com.sit708.learningassistant.utils.DummyDataProvider;
import com.sit708.learningassistant.utils.SessionManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InterestsActivity extends AppCompatActivity {

    private ChipGroup chipGroup;
    private final List<String> selectedInterests = new ArrayList<>();
    private String username, email;
    private AppDatabase db;
    private SessionManager session;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");

        chipGroup = findViewById(R.id.chipGroup);
        MaterialButton btnNext = findViewById(R.id.btnNext);

        populateChips();

        btnNext.setOnClickListener(v -> {
            if (selectedInterests.isEmpty()) {
                Toast.makeText(this, "Please select at least one topic", Toast.LENGTH_SHORT).show();
                return;
            }
            saveInterestsAndProceed();
        });
    }

    private void populateChips() {
        List<String> topics = DummyDataProvider.getTopics();
        for (String topic : topics) {
            Chip chip = new Chip(this);
            chip.setText(topic);
            chip.setCheckable(true);
            chip.setTextColor(getColor(R.color.white));
            chip.setChipBackgroundColorResource(R.color.chip_unselected);
            chip.setChipStrokeColorResource(R.color.white);
            chip.setChipStrokeWidth(2f);
            chip.setCheckedIconVisible(false);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (selectedInterests.size() >= 10) {
                        chip.setChecked(false);
                        Toast.makeText(this, "Max 10 topics", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedInterests.add(topic);
                    chip.setChipBackgroundColorResource(R.color.chip_selected);
                } else {
                    selectedInterests.remove(topic);
                    chip.setChipBackgroundColorResource(R.color.chip_unselected);
                }
            });

            chipGroup.addView(chip);
        }
    }

    private void saveInterestsAndProceed() {
        JSONArray interestsJson = new JSONArray();
        for (String interest : selectedInterests) {
            interestsJson.put(interest);
        }
        String interestsStr = interestsJson.toString();

        executor.execute(() -> {
            User user = db.userDao().findByUsername(username);
            if (user != null) {
                user.interests = interestsStr;
                db.userDao().insert(user); // Room will replace on conflict if we set strategy
            }
            handler.post(() -> {
                session.saveUser(username, email != null ? email : "", interestsStr);
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
