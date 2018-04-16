package me.ayahya.aesirr.twisentials.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.util.HashMap;

import me.ayahya.aesirr.twisentials.utils.SharedPrefs;
import retrofit2.Call;

public class TwitterService {
    private static final String TAG = TwitterService.class.getSimpleName();
    private FirestoreService firestoreService;
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private HashMap<String, Object> followers = new HashMap<>();
    private HashMap<String, Object> friends = new HashMap<>();
    private SharedPrefs sharedPrefs;
    private me.ayahya.aesirr.twisentials.models.User user;

    public Callback<TwitterSession> setCallback(final Activity activity) {
        return new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                sharedPrefs = SharedPrefs.newInstance(activity.getApplicationContext());
                setCurrentUser(user);
                firebaseAuthService.handleTwitterSession(activity, result.data);
                storeUser(activity.getApplicationContext());
                // Update Firestore
            }

            @Override
            public void failure(TwitterException exception) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "TwitterService:setCallback");
                FirebaseCrash.report(exception);
                // updateUI(null)
            }
        };
    }

    private void storeUser(Context context) {
        firestoreService = new FirestoreService();
        Call<User> call = TwitterCore.getInstance()
                .getApiClient()
                .getAccountService()
                .verifyCredentials(true, true, true);

        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                String biggerAvi = result.data.profileImageUrlHttps
                        .substring(0, result.data.profileImageUrlHttps.length() - 11)
                        .concat(".jpg");
                String biggerBanner = result.data.profileBannerUrl
                        .substring(0, result.data.profileBannerUrl.length())
                        .concat("/1500x500");

                checkForNullKeys(result);
//                trackUserRatios(result.data.followersCount, result.data.friendsCount);
                // Default is '_original' which is too small
                me.ayahya.aesirr.twisentials.models.User currentUser =
                        new me.ayahya.aesirr.twisentials.models.User(
                                biggerAvi,  biggerBanner, result.data.createdAt,
                                result.data.description, result.data.email, result.data.lang,
                                result.data.name, result.data.screenName,  result.data.idStr,
                                followers, friends, result.data.favouritesCount);
                sharedPrefs.setSharedPrefs(biggerAvi, biggerBanner, result.data.idStr, result.data.name,
                        result.data.email, String.valueOf(result.data.favouritesCount), sharedPrefs.getFollowersCount(),
                        sharedPrefs.getFriendsCount(), sharedPrefs.getFollowersColor(), sharedPrefs.getFriendsColor());
                trackUserRatios(context.getApplicationContext(), currentUser);
                firestoreService.pushUserDocument(currentUser);
                setCurrentUser(user);
            }

            @Override
            public void failure(TwitterException exception) {
                FirebaseCrash.logcat(Log.WARN, TAG, "Error:storeNewUser: " + exception);
                FirebaseCrash.report(exception);
            }
        });
    }

    public void trackUserRatios(Context context, me.ayahya.aesirr.twisentials.models.User cUser) {// int cFollowerCount, int cFriendCount) {
        sharedPrefs = SharedPrefs.newInstance(context.getApplicationContext());
        int pFollowerCount = Integer.parseInt(sharedPrefs.getFollowersCount());
        int pFriendCount = Integer.parseInt(sharedPrefs.getFriendsCount());
        int cFollowerCount = Integer.parseInt(cUser.getFollowers().get("count").toString());
        int cFriendCount = Integer.parseInt(cUser.getFriends().get("count").toString());

        if (pFollowerCount < cFollowerCount) {
            followers.put("color", "green");
        } else if (pFollowerCount > cFollowerCount) {
            followers.put("color", "red");
        } else {
            followers.put("color", "#9e9e9e");
        }

        if (pFriendCount < cFriendCount) {
            friends.put("color", "green");
        } else if (pFriendCount > cFriendCount) {
            friends.put("color", "red");
        } else {
            friends.put("color", "#9e9e9e");
        }

        sharedPrefs.setFollowersCount(String.valueOf(cFollowerCount));
        sharedPrefs.setFriendsCount(String.valueOf(cFriendCount));
        sharedPrefs.setFollowersColor(followers.get("color").toString());
        sharedPrefs.setFriendsColor(friends.get("color").toString());
    }

    private void checkForNullKeys(Result<User> result) {
        if (!followers.containsKey("count") || !friends.containsKey("count")) {
            followers.put("count", result.data.followersCount);
            friends.put("count", result.data.friendsCount);

            sharedPrefs.setFollowersCount(String.valueOf(result.data.followersCount));
            sharedPrefs.setFriendsCount(String.valueOf(result.data.friendsCount));
        }

        if (!followers.containsKey("color") || !friends.containsKey("color")) {
            followers.put("color", "#9e9e9e");
            friends.put("color", "#9e9e9e");

            sharedPrefs.setFollowersColor("#9e9e9e");
            sharedPrefs.setFriendsColor("#9e9e9e");
        }
    }

    public void setCurrentUser(me.ayahya.aesirr.twisentials.models.User currentUser) { this.user = currentUser; }
    public me.ayahya.aesirr.twisentials.models.User getCurrentUser() { return this.user; }
}
