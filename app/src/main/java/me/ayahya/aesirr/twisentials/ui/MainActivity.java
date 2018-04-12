package me.ayahya.aesirr.twisentials.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirestoreService firestoreService = new FirestoreService();
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
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

        // Set counters into TextViews
        followersCount
                .setTextColor(Color.parseColor(sharedPrefs.getFollowersColor()));
        friendsCount
                .setTextColor(Color.parseColor(sharedPrefs.getFriendsColor()));
        followersCount
                .setText(sharedPrefs.getFollowersCount());
        friendsCount
                .setText(sharedPrefs.getFriendsCount());

            // Track counter changes & change colors respectively
//            firestoreService.trackRatio(getApplicationContext(), followersCount);
//            firestoreService.ratioOnChangeListener(followersCount);
//            followersCount.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    // Set counters into TextViews
//                    followersCount
//                            .setTextColor(Color.parseColor(sharedPrefs.getFollowersColor()));
//                    followersCount
//                            .setText(sharedPrefs.getFollowersCount());
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) { }
//            });
//        }
    }

    private void updateRatioUI() {

    }
}
