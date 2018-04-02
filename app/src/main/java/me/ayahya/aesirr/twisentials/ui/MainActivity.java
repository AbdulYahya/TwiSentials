package me.ayahya.aesirr.twisentials.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import retrofit2.Call;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirestoreService firestoreService = new FirestoreService();
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuthService.getFirebaseAuthInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService accountService = twitterApiClient.getAccountService();
        Call<User> call = accountService.verifyCredentials(true, true, true);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(final Result<User> result) {
                FirebaseFirestore fsDB = firestoreService.getFsDB();
                DocumentReference docRef = fsDB.collection("users").document(result.data.idStr);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TextView followersCount = findViewById(R.id.followers_count);
                        TextView friendsCount = findViewById(R.id.friends_count);

                        friendsCount.setText(String.valueOf(result.data.friendsCount));
                        followersCount.setText(String.valueOf(result.data.followersCount));
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

    }
}
