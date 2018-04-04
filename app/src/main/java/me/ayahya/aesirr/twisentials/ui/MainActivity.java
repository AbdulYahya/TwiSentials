package me.ayahya.aesirr.twisentials.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.models.User;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;

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
            firestoreService.trackRatio(getApplicationContext());
        }
    }
}
