package com.rini.keuanganharian;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREF_NAME = "keuangan_harian_preferences";
    private static final String KEY_SHOW_BALANCE = "show_balance";
    private static final String KEY_USER_NAME = "user_name";

    private final SharedPreferences preferences;

    public PrefsManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isBalanceVisible() {
        return preferences.getBoolean(KEY_SHOW_BALANCE, true);
    }

    public void setBalanceVisible(boolean visible) {
        // apply() menyimpan perubahan secara asynchronous tanpa memblokir UI.
        preferences.edit().putBoolean(KEY_SHOW_BALANCE, visible).apply();
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "Teman");
    }

    public void setUserName(String name) {
        preferences.edit().putString(KEY_USER_NAME, name).apply();
    }
}
