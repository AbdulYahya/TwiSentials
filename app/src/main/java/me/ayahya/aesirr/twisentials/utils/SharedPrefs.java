package me.ayahya.aesirr.twisentials.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefs {
    private static SharedPrefs savedInstanceState;
    private static SharedPreferences sharedPreferences;

    public static SharedPrefs newInstance(Context context) {
        if (savedInstanceState == null) {
            savedInstanceState = new SharedPrefs();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return savedInstanceState;
    }

    public String getImagePath(String imageKey) { return sharedPreferences.getString(imageKey, "-1"); }

    public void setImagePath(String imageKey, String imagePath) {
        sharedPreferences.edit()
                .putString(imageKey, imagePath)
                .apply();
    }

    public String getUserId() { return sharedPreferences.getString("userId", null); }

    public void setUserId(String userId) {
        sharedPreferences.edit()
                .putString("userId", userId)
                .apply();
    }

    public String getUserName() { return sharedPreferences.getString("userName", null); }

    public void setUserName(String userName) {
        sharedPreferences.edit()
                .putString("userName", userName)
                .apply();
    }

    public String getUserEmail() { return sharedPreferences.getString("userEmail", null); }

    public void setUserEmail(String userEmail) {
        sharedPreferences.edit()
                .putString("userEmail", userEmail)
                .apply();
    }

    public String getFavoritesCount() { return sharedPreferences.getString("favoritesCount", null); }

    public void setFavoritesCount(String favoritesCount) {
        sharedPreferences.edit()
                .putString("favoritesCount", favoritesCount)
                .apply();
    }

    public String getFollowersCount() { return sharedPreferences.getString("followersCount", null); }

    public void setFollowersCount(String followersCount) {
        sharedPreferences.edit()
                .putString("followersCount", followersCount)
                .apply();
    }

    public String getFriendsCount() { return sharedPreferences.getString("friendsCount", null); }

    public void setFriendsCount(String friendsCount) {
        sharedPreferences.edit()
                .putString("friendsCount", friendsCount)
                .apply();
    }
}
