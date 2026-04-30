package com.sit708.learningassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sit708.learningassistant.R;
import com.sit708.learningassistant.models.Question;
import com.sit708.learningassistant.network.GeminiService;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private final List<Question> questions;
    private final String topic;
    private final GeminiService geminiService;

    public QuestionAdapter(List<Question> questions, String topic) {
        this.questions = questions;
        this.topic = topic;
        this.geminiService = new GeminiService();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);

        holder.tvQuestionNumber.setText("Question " + (position + 1));
        holder.tvQuestionText.setText(question.getQuestionText());

        // Build radio buttons for each option
        holder.radioGroup.removeAllViews();
        List<String> options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            RadioButton rb = new RadioButton(holder.itemView.getContext());
            rb.setText(options.get(i));
            rb.setTextColor(holder.itemView.getContext().getColor(R.color.white));
            rb.setTextSize(14f);
            rb.setButtonTintList(android.content.res.ColorStateList.valueOf(
                    holder.itemView.getContext().getColor(R.color.accent)));
            final int idx = i;
            rb.setId(View.generateViewId());
            holder.radioGroup.addView(rb);
            if (question.getSelectedAnswerIndex() == i) {
                rb.setChecked(true);
            }
        }

        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    question.setSelectedAnswerIndex(i);
                    break;
                }
            }
        });

        // Reset hint visibility on rebind
        holder.layoutHint.setVisibility(View.GONE);
        holder.pbHintLoading.setVisibility(View.GONE);
        holder.tvHintError.setVisibility(View.GONE);

        // Hint button click — LLM Utility 1
        holder.btnHint.setOnClickListener(v -> {
            holder.btnHint.setEnabled(false);
            holder.btnHint.setText("Loading…");
            holder.pbHintLoading.setVisibility(View.VISIBLE);
            holder.layoutHint.setVisibility(View.GONE);
            holder.tvHintError.setVisibility(View.GONE);

            geminiService.generateHint(question.getQuestionText(), topic,
                    new GeminiService.GeminiCallback() {
                        @Override
                        public void onSuccess(String prompt, String response) {
                            holder.pbHintLoading.setVisibility(View.GONE);
                            holder.tvHintPrompt.setText(prompt);
                            holder.tvHintResponse.setText(response);
                            holder.layoutHint.setVisibility(View.VISIBLE);
                            holder.layoutHint.setAlpha(0f);
                            holder.layoutHint.animate().alpha(1f).setDuration(400).start();
                            holder.btnHint.setEnabled(true);
                            holder.btnHint.setText("💡 Hint shown");
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            holder.pbHintLoading.setVisibility(View.GONE);
                            holder.tvHintError.setText("⚠ " + errorMessage);
                            holder.tvHintError.setVisibility(View.VISIBLE);
                            holder.btnHint.setEnabled(true);
                            holder.btnHint.setText("💡 Get Hint");
                        }
                    });
        });

        // Animate card in
        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(position * 80L)
                .start();
    }

    @Override
    public int getItemCount() { return questions.size(); }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvQuestionText, tvHintPrompt, tvHintResponse, tvHintError;
        MaterialButton btnHint;
        RadioGroup radioGroup;
        LinearLayout layoutHint;
        ProgressBar pbHintLoading;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            tvHintPrompt = itemView.findViewById(R.id.tvHintPrompt);
            tvHintResponse = itemView.findViewById(R.id.tvHintResponse);
            tvHintError = itemView.findViewById(R.id.tvHintError);
            btnHint = itemView.findViewById(R.id.btnHint);
            radioGroup = itemView.findViewById(R.id.radioGroupAnswers);
            layoutHint = itemView.findViewById(R.id.layoutHint);
            pbHintLoading = itemView.findViewById(R.id.pbHintLoading);
        }
    }
}
