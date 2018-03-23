package me.ayahya.aesirr.twisentials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.TwitterService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    final TwitterService twitterService = new TwitterService();

    @BindView(R.id.status) TextView statusTextView;
    @BindView(R.id.detail) TextView detailTextView;
    @BindView(R.id.button_twitter_login) TwitterLoginButton twitterLoginButton;
    @BindView(R.id.button_twitter_signout) Button twitterSignOutButton;

    protected final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.mobile_ads_APP_ID));
        ButterKnife.bind(this);


        twitterSignOutButton.setOnClickListener(this);
        firebaseAuthService.getInstance();
        twitterLoginButton.setCallback(twitterService.setCallback());
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass activity result to Twitter Login button
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v == twitterSignOutButton) { firebaseAuthService.completeSignout(); }
    }
}
