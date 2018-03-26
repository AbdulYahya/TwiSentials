package me.ayahya.aesirr.twisentials.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.button_twitter_signout)
    Button twitterSignoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.mobile_ads_APP_ID));
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuthService.getFirebaseAuthInstance();
        twitterSignoutButton.setOnClickListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuthService.getFirebaseAuthInstance().getCurrentUser();
    }

    private void signOut() {
        firebaseAuthService.completeSignout();
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public void onClick(View v) {
        if (v == twitterSignoutButton) {
            signOut();
        }
    }
}
