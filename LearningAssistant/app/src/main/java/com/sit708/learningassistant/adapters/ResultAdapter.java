package com.sit708.learningassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sit708.learningassistant.R;
import com.sit708.learningassistant.models.Question;
import com.sit708.learningassistant.network.GeminiService;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private final List<Question> questions;
    private final String topic;
    private final GeminiService geminiService;

    public ResultAdapter(List<Question> questions, String topic) {
        this.questions = questions;
        this.topic = topic;
        this.geminiService = new GeminiService();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Question question = questions.get(position);
        boolean correct = question.isCorrect();

        holder.tvQuestionNumber.setText((position + 1) + ". " + question.getQuestionText());
        holder.tvYourAnswer.setText("Your answer: " + question.getSelectedAnswerText());
        holder.tvCorrectAnswer.setText("✓ Correct: " + question.getCorrectAnswerText());

        if (correct) {
            holder.tvCorrectBadge.setText("✓ Correct");
            holder.tvCorrectBadge.setBackgroundResource(R.drawable.bg_hint);
            holder.tvCorrectBadge.setTextColor(
                    holder.itemView.getContext().getColor(R.color.accent_green));
        } else {
            holder.tvCorrectBadge.setText("✗ Incorrect");
            holder.tvCorrectBadge.setBackgroundResource(R.drawable.bg_badge);
            holder.tvCorrectBadge.setTextColor(
                    holder.itemView.getContext().getColor(R.color.error_red));
        }

        // Auto-load AI explanation — LLM Utility 2
        holder.layoutExplanation.setVisibility(View.GONE);
        holder.pbExplanationLoading.setVisibility(View.VISIBLE);
        holder.tvExplanationError.setVisibility(View.GONE);

        geminiService.explainAnswer(
                question.getQuestionText(),
                question.getSelectedAnswerText(),
                question.getCorrectAnswerText(),
                correct,
                topic,
                new GeminiService.GeminiCallback() {
                    @Override
                    public void onSuccess(String prompt, String response) {
                        holder.pbExplanationLoading.setVisibility(View.GONE);
                        holder.tvExplanationPrompt.setText(prompt);
                        holder.tvExplanationText.setText(response);
                        holder.layoutExplanation.setVisibility(View.VISIBLE);
                        holder.layoutExplanation.setAlpha(0f);
                        holder.layoutExplanation.animate().alpha(1f).setDuration(500).start();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        holder.pbExplanationLoading.setVisibility(View.GONE);
                        holder.tvExplanationError.setText("⚠ Could not load explanation: " + errorMessage);
                        holder.tvExplanationError.setVisibility(View.VISIBLE);
                    }
                });

        // Animate card in
        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(position * 100L)
                .start();
    }

    @Override
    public int getItemCount() { return questions.size(); }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvCorrectBadge, tvYourAnswer,
                tvCorrectAnswer, tvExplanationPrompt, tvExplanationText, tvExplanationError;
        LinearLayout layoutExplanation;
        ProgressBar pbExplanationLoading;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvCorrectBadge = itemView.findViewById(R.id.tvCorrectBadge);
            tvYourAnswer = itemView.findViewById(R.id.tvYourAnswer);
            tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
            tvExplanationPrompt = itemView.findViewById(R.id.tvExplanationPrompt);
            tvExplanationText = itemView.findViewById(R.id.tvExplanationText);
            tvExplanationError = itemView.findViewById(R.id.tvExplanationError);
            layoutExplanation = itemView.findViewById(R.id.layoutExplanation);
            pbExplanationLoading = itemView.findViewById(R.id.pbExplanationLoading);
        }
    }
}
