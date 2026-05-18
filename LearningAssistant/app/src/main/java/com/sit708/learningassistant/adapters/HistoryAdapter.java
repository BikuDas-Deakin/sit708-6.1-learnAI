package com.sit708.learningassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sit708.learningassistant.R;
import com.sit708.learningassistant.models.QuizAttempt;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Task 10.1 – New adapter.
 * Displays each QuizAttempt as a card; tapping expands per-question details.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<QuizAttempt> attempts;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.getDefault());

    public HistoryAdapter(List<QuizAttempt> attempts) {
        this.attempts = attempts;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        QuizAttempt attempt = attempts.get(position);

        holder.tvAttemptNumber.setText((position + 1) + ". " + attempt.topic);
        holder.tvDate.setText(sdf.format(new Date(attempt.timestamp)));
        holder.tvScore.setText(attempt.correctAnswers + "/" + attempt.totalQuestions + " correct");

        // Toggle expand on click
        boolean expanded = attempt.totalQuestions > 0
                && holder.layoutDetail.getVisibility() == View.VISIBLE;
        holder.itemView.setOnClickListener(v -> {
            if (holder.layoutDetail.getVisibility() == View.GONE) {
                holder.layoutDetail.setVisibility(View.VISIBLE);
                holder.tvExpand.setText("▲");
                populateDetails(holder, attempt);
            } else {
                holder.layoutDetail.setVisibility(View.GONE);
                holder.tvExpand.setText("▼");
            }
        });

        // Reset on rebind
        holder.layoutDetail.setVisibility(View.GONE);
        holder.tvExpand.setText("▼");

        // Animate in
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).setStartDelay(position * 60L).start();
    }

    private void populateDetails(@NonNull HistoryViewHolder holder, QuizAttempt attempt) {
        holder.layoutQuestions.removeAllViews();
        try {
            JSONArray qArr = new JSONArray(attempt.questionsJson);
            JSONArray sArr = new JSONArray(attempt.selectedAnswersJson);
            JSONArray cArr = new JSONArray(attempt.correctAnswersJson);

            for (int i = 0; i < qArr.length(); i++) {
                String qText    = qArr.optString(i, "Question " + (i + 1));
                String selected = sArr.optString(i, "—");
                String correct  = cArr.optString(i, "—");
                boolean isRight = selected.equals(correct);

                TextView tv = new TextView(holder.itemView.getContext());
                tv.setText((i + 1) + ". " + qText
                        + "\n   ● " + selected + (isRight ? "  ✓" : "  ✗")
                        + "\n   ✓ " + correct);
                tv.setTextColor(holder.itemView.getContext()
                        .getColor(isRight ? R.color.accent_green : R.color.white));
                tv.setTextSize(13f);
                tv.setPadding(0, 12, 0, 12);
                holder.layoutQuestions.addView(tv);
            }
        } catch (JSONException e) {
            TextView tv = new TextView(holder.itemView.getContext());
            tv.setText("Could not load detail.");
            holder.layoutQuestions.addView(tv);
        }
    }

    @Override
    public int getItemCount() { return attempts.size(); }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvAttemptNumber, tvDate, tvScore, tvExpand;
        LinearLayout layoutDetail, layoutQuestions;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAttemptNumber  = itemView.findViewById(R.id.tvAttemptNumber);
            tvDate           = itemView.findViewById(R.id.tvDate);
            tvScore          = itemView.findViewById(R.id.tvScore);
            tvExpand         = itemView.findViewById(R.id.tvExpand);
            layoutDetail     = itemView.findViewById(R.id.layoutDetail);
            layoutQuestions  = itemView.findViewById(R.id.layoutQuestions);
        }
    }
}
