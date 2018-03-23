package me.ayahya.aesirr.twisentials.services;

import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.services.AccountService;

public class FirebaseAuthService {
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "FirebaseAuthService";

    public void getInstance() {  FirebaseAuth.getInstance(); }

    public void completeSignout() {
        firebaseAuth.signOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }
}
