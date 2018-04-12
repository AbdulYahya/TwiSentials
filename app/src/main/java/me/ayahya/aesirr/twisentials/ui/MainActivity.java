package me.ayahya.aesirr.twisentials.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPrefs = SharedPrefs.newInstance(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView followersCount = findViewById(R.id.followers_count);
        TextView friendsCount = findViewById(R.id.friends_count);

        followersCount
                .setTextColor(Color.parseColor(sharedPrefs.getFollowersColor()));
        friendsCount
                .setTextColor(Color.parseColor(sharedPrefs.getFriendsColor()));
        followersCount
                .setText(sharedPrefs.getFollowersCount());
        friendsCount
                .setText(sharedPrefs.getFriendsCount());

    }

}
