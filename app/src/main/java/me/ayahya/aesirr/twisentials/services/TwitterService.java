package me.ayahya.aesirr.twisentials.services;

import android.app.Activity;
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

import retrofit2.Call;

public class TwitterService {
    private static final String TAG = TwitterService.class.getSimpleName();
    private FirestoreService firestoreService = new FirestoreService();
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();

    private HashMap<String, Object> followers = new HashMap<>();
    private HashMap<String, Object> friends = new HashMap<>();

    public Callback<TwitterSession> setCallback(final Activity activity) {
        return new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
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

    private void storeNewUser() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService statusService = twitterApiClient.getAccountService();
        Call<User> call = statusService.verifyCredentials(true, true, true);
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

                followers.put("color", "#9e9e9e");
                followers.put("count", String.valueOf(result.data.followersCount));
                friends.put("color", "#9e9e9e");
                friends.put("count", String.valueOf(result.data.friendsCount));

                // New User
                me.ayahya.aesirr.twisentials.models.User currentUser =
                        new me.ayahya.aesirr.twisentials.models.User(
                                biggerAvi,  biggerBanner, result.data.createdAt,
                                result.data.description, result.data.email, result.data.lang,
                                result.data.name, result.data.screenName,  result.data.idStr,
                                followers, friends, result.data.favouritesCount);
                firestoreService.newUserDocument(currentUser);
            }

            @Override
            public void failure(TwitterException exception) {
                FirebaseCrash.logcat(Log.WARN, TAG, "Error:storeNewUser: " + exception);
                FirebaseCrash.report(exception);
            }
        });
    }
}
