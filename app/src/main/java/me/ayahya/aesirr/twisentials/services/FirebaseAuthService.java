package me.ayahya.aesirr.twisentials.services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


public class FirebaseAuthService {
    private static final String TAG = FirebaseAuthService.class.getSimpleName();
    private FirebaseAuth firebaseAuth;

    public FirebaseAuthService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getFirebaseAuth() { return firebaseAuth; }

    public FirebaseAuth getFirebaseAuthInstance() { return FirebaseAuth.getInstance(); }

    public void handleTwitterSession(Activity activity, TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseCrash.logcat(Log.DEBUG, TAG, "signInWithCredential:success");
                        // updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        FirebaseCrash.logcat(Log.WARN, TAG, "signInWithCredential:failure " + task.getException());
                        FirebaseCrash.report(task.getException());
                    }
                });
    }

    public void completeSignout() {
        getFirebaseAuthInstance().signOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }
}
