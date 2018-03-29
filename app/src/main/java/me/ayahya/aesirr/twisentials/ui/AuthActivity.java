package me.ayahya.aesirr.twisentials.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.TwitterService;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private TwitterService twitterService = new TwitterService();
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirebaseAuth firebaseAuth = firebaseAuthService.getFirebaseAuth();
    private FirebaseAuth.AuthStateListener authStateListener;

    @BindView(R.id.button_twitter_login)
    TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuthService.getFirebaseAuthInstance();
        authStateListener();
        twitterLoginButton.setCallback(twitterService.setCallback(AuthActivity.this));
    }
    private void authStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuthService.getFirebaseAuth().getCurrentUser();
                if (user != null) {
                    String welcome = "Welcome, " + user.getDisplayName() + "!";
                    Toast.makeText(AuthActivity.this, welcome, Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the Twitter login button.
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

    }
}

