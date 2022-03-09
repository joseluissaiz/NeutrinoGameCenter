package com.overshade.neutrinogamecenter;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginCredentials {

    private static final String FILE_SAVED_CREDENTIALS = "saved_credentials";
    private static final String KEY_SAVED_USERNAME = "saved_username";
    private static final String KEY_SAVED_PASSWORD = "saved_password";

    public static String getUsernameSavedCredentials(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                FILE_SAVED_CREDENTIALS, Context.MODE_PRIVATE
        );
        return sharedPreferences.getString(KEY_SAVED_USERNAME, null);
    }

    public static String getPasswordSavedCredentials(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                FILE_SAVED_CREDENTIALS, Context.MODE_PRIVATE
        );
        return sharedPreferences.getString(KEY_SAVED_PASSWORD, null);
    }

    public static void saveUserCredentials(String username, String password, Context context) {
        if (password.endsWith("\n")) {
            password = password.substring(0, password.length()-1);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                FILE_SAVED_CREDENTIALS, Context.MODE_PRIVATE
        );
        sharedPreferences.edit().putString(KEY_SAVED_USERNAME, username).apply();
        sharedPreferences.edit().putString(KEY_SAVED_PASSWORD, password).apply();
    }

    public static void deleteCredentials(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                FILE_SAVED_CREDENTIALS, Context.MODE_PRIVATE
        );
        sharedPreferences.edit().remove(KEY_SAVED_USERNAME).apply();
        sharedPreferences.edit().remove(KEY_SAVED_PASSWORD).apply();
    }

}
