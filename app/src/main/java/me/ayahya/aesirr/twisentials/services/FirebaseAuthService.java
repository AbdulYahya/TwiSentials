package me.ayahya.aesirr.twisentials.services;

import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.services.AccountService;

public class FirebaseAuthService {
    private static final String TAG = "FirebaseAuthService";

    private FirebaseAuth firebaseAuth;

//    private TwitterApiClient twitterApiClient;
//    AccountService accountService;

    public FirebaseAuth getFirebaseAuth() { return firebaseAuth; }
    public void setFirebaseAuth(FirebaseAuth firebaseAuth) { this.firebaseAuth = firebaseAuth; }
}
