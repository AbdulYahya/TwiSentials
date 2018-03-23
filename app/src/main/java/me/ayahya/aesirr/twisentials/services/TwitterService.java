package me.ayahya.aesirr.twisentials.services;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

public class TwitterService {
    private static final String TAG = "TwitterService";

    public Callback<TwitterSession> setCallback() {
        return new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, " " + result.data.getUserName());
                Log.e(TAG, TAG);
            }

            @Override
            public void failure(TwitterException exception) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "TwitterService:setCallback");
                FirebaseCrash.report(exception);
            }
        };
    }
}
