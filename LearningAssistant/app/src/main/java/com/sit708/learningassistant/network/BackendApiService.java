package com.sit708.learningassistant.network;

import com.sit708.learningassistant.models.QuizResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class BackendApiService {

    // 10.0.2.2 is the Android emulator alias for localhost on the host machine
    private static final String BASE_URL = "http://10.0.2.2:5000/";

    private static BackendApiService instance;
    private final QuizApi quizApi;

    public interface QuizApi {
        @GET("getQuiz")
        Call<QuizResponse> getQuiz(@Query("topic") String topic);
    }

    private BackendApiService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.MINUTES)  // Llama can be slow — match tutor's setting
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        quizApi = retrofit.create(QuizApi.class);
    }

    public static synchronized BackendApiService getInstance() {
        if (instance == null) {
            instance = new BackendApiService();
        }
        return instance;
    }

    public QuizApi getApi() { return quizApi; }
}
