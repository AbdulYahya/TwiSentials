package me.ayahya.aesirr.twisentials.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import java.util.HashMap;

import me.ayahya.aesirr.twisentials.utils.SharedPrefs;
import retrofit2.Call;

public class TwitterService {
    private static final String TAG = TwitterService.class.getSimpleName();
    private static TwitterService savedInstanceState;
    private static FirestoreService firestoreService = new FirestoreService();;
    private static FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private static SharedPrefs sharedPrefs;
    private static Call<User> call;

    private HashMap<String, Object> followers = new HashMap<>();
    private HashMap<String, Object> friends = new HashMap<>();
    private Result<User> userResult;
    private me.ayahya.aesirr.twisentials.models.User User;

//    public TwitterService() {
//        firestoreService = new FirestoreService();
//        firebaseAuthService = new FirebaseAuthService();
//
//        followers = new HashMap<>();
//        friends = new HashMap<>();
//
//        twitterApiClient = TwitterCore.getInstance().getApiClient();
//    }

    public static TwitterService newInstance(Context context) {
        if (savedInstanceState == null) {
            savedInstanceState = new TwitterService();
            sharedPrefs = SharedPrefs.newInstance(context.getApplicationContext());
        }

        return savedInstanceState;
    }

    public Callback<TwitterSession> setCallback(final Activity activity) {
        return new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
//                sharedPrefs = SharedPrefs.newInstance(activity.getApplicationContext());
                newInstance(activity);
                firebaseAuthService.handleTwitterSession(activity, result.data);
                storeNewUser();
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

    public Result<User> currentUser() {
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                userResult = result;
            }

            @Override
            public void failure(TwitterException exception) {
                FirebaseCrash.logcat(Log.WARN, "TwitterService:currentUser", exception.getMessage());
            }
        });

        return userResult;
    }

    private void storeNewUser() {
        call =  TwitterCore.getInstance()
                .getApiClient()
                .getAccountService()
                .verifyCredentials(true, true, true);
        
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                // Default is '_original' which is too small
                String biggerAvi = result.data.profileImageUrlHttps
                        .substring(0, result.data.profileImageUrlHttps.length() - 11)
                        .concat(".jpg");
                String biggerBanner = result.data.profileBannerUrl
                        .substring(0, result.data.profileBannerUrl.length())
                        .concat("/1500x500");

                // This method is only used to store new users
                // move this logic over into a method in the Firestore service
                // note: have a onChange listener wrap over the method
                int currentFollowerCount = result.data.followersCount;
                // Add current follower count to hashmap
                followers.put("count", String.valueOf(currentFollowerCount));

                if (sharedPrefs.getFollowersCount() != null) {
                    int prevFollowerCount = Integer.parseInt(sharedPrefs.getFollowersCount());

                    if (prevFollowerCount < currentFollowerCount) {
                        followers.put("color", "green");
                    } else if (prevFollowerCount > currentFollowerCount) {
                        followers.put("color", "red");
                    } else {
                        followers.put("color", "#9e9e9e");
                    }
                } else {
                    followers.put("color", "#9e9e9e");
                }

                friends.put("color", "#9e9e9e");
                friends.put("count", String.valueOf(result.data.friendsCount));

                // New User
                me.ayahya.aesirr.twisentials.models.User currentUser =
                        new me.ayahya.aesirr.twisentials.models.User(
                                biggerAvi,  biggerBanner, result.data.createdAt,
                                result.data.description, result.data.email, result.data.lang,
                                result.data.name, result.data.screenName,  result.data.idStr,
                                followers, friends, result.data.favouritesCount);
                sharedPrefs.setSharedPrefs(biggerAvi, biggerBanner, result.data.idStr, result.data.name,
                        result.data.email, String.valueOf(result.data.favouritesCount), String.valueOf(result.data.followersCount),
                        String.valueOf(result.data.friendsCount), followers.get("color").toString(), friends.get("color").toString());
                firestoreService.newUserDocument(currentUser);
                setCurrentUser(currentUser);
            }

            @Override
            public void failure(TwitterException exception) {
                FirebaseCrash.logcat(Log.WARN, TAG, "Error:storeNewUser: " + exception);
                FirebaseCrash.report(exception);
            }
        });
    }

    private void setCurrentUser(me.ayahya.aesirr.twisentials.models.User currentUser) { this.User = currentUser; }
    public me.ayahya.aesirr.twisentials.models.User getCurrentUser() { return this.User; }
}
