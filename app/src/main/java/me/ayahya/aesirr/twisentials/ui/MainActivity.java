package me.ayahya.aesirr.twisentials.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;
import retrofit2.Call;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirestoreService firestoreService = new FirestoreService();
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirebaseAuth firebaseAuth;
    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        firebaseAuth = firebaseAuthService.getFirebaseAuthInstance();
        sharedPrefs = SharedPrefs.newInstance(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            firebaseAuthService.completeSignout();
            Intent authIntent = new Intent(MainActivity.this, AuthActivity.class);
            authIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(authIntent);
            finish();
        } else {
            TextView followersCount = findViewById(R.id.followers_count);
            TextView friendsCount = findViewById(R.id.friends_count);

            followersCount.setText(sharedPrefs.getFollowersCount());
            friendsCount.setText(sharedPrefs.getFriendsCount());
        }
    }
}
