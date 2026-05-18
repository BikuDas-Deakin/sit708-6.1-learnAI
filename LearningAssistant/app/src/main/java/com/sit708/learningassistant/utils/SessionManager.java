package com.sit708.learningassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME   = "LearnAISession";
    private static final String KEY_USERNAME  = "username";
    private static final String KEY_EMAIL     = "email";
    private static final String KEY_INTERESTS = "interests";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_TIER      = "tier";   // Added Task 10.1

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUser(String username, String email, String interests) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_INTERESTS, interests);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    /** Persist the tier locally after a purchase so UI updates immediately. */
    public void saveTier(String tier) {
        editor.putString(KEY_TIER, tier);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "Student");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getInterests() {
        return prefs.getString(KEY_INTERESTS, "");
    }

    public String getTier() {
        return prefs.getString(KEY_TIER, "Free");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
