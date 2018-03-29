package me.ayahya.aesirr.twisentials.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;

public class SharedPrefs {
    protected static SharedPrefs savedInstanceState;
    private SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;

    public static SharedPrefs newInstance(Context context) {
        if (savedInstanceState == null) {
            savedInstanceState = new SharedPrefs();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return savedInstanceState;
    }

    public void setImagePath(String imageKey, String imagePath) {
        sharedPreferences.edit().putString(imageKey, imagePath).apply();
    }

    public String getImagePath(String imageKey) {
        return sharedPreferences.getString(imageKey, "-1");
    }

    public void addToSharedPrefs(String userName, String userEmail, String followersCount,
                                 String friendsCount, String favoritesCount) {
        editor.putString("userName", userName).apply();
        editor.putString("userEmail", userEmail).apply();
        editor.putString("followersCount", followersCount).apply();
        editor.putString("friendsCount", friendsCount).apply();
        editor.putString("favoritesCount", favoritesCount).apply();
    }
}
