package me.ayahya.aesirr.twisentials.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.ads.MobileAds;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.button_twitter_signout) Button twitterSignOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.mobile_ads_APP_ID));
        ButterKnife.bind(this);

        twitterSignOutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == twitterSignOutButton) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
        }
    }
}
