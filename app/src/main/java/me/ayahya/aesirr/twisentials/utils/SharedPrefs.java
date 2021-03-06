package me.ayahya.aesirr.twisentials.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.ayahya.aesirr.twisentials.ui.MainActivity;

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
    public String getFollowersColor() { return sharedPreferences.getString("followersColor", null); }

    public void setFollowersColor(String followersColor) {
        sharedPreferences.edit()
                .putString("followersColor", followersColor)
                .apply();
    }

    public String getFollowersCount() { return sharedPreferences.getString("followersCount", null); }

    public void setFollowersCount(String followersCount) {
        sharedPreferences.edit()
                .putString("followersCount", followersCount)
                .apply();
    }

    public String getFriendsColor() { return sharedPreferences.getString("friendsColor", null); }

    public void setFriendsColor(String friendsColor) {
        sharedPreferences.edit()
                .putString("friendsColor", friendsColor)
                .apply();
    }

    public String getFriendsCount() { return sharedPreferences.getString("friendsCount", null); }

    public void setFriendsCount(String friendsCount) {
        sharedPreferences.edit()
                .putString("friendsCount", friendsCount)
                .apply();
    }

    public void setSharedPrefs(String userAvi, String userBanner, String userId,
                                String userName, String userEmail, String favoritesCount,
                                String followersCount, String friendsCount,
                                String followersColor, String friendsColor) {
        setImagePath("userAvi", userAvi);
        setImagePath("userBanner", userBanner);
        setUserId(userId);
        setUserName(userName);
        setUserEmail(userEmail);
        setFavoritesCount(favoritesCount);
        setFollowersColor(followersColor);
        setFollowersCount(followersCount);
        setFriendsColor(friendsColor);
        setFriendsCount(friendsCount);
    }
}
