package me.ayahya.aesirr.twisentials.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.services.AccountService;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.CircleTransform;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.fragments.UserProfileFragment;
import me.ayahya.aesirr.twisentials.models.User;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int MAX_HEIGHT = 400;
    private static final int MAX_WIDTH = 400;
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirestoreService firestoreService = new FirestoreService();
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.button_twitter_signout)
    Button twitterSignoutButton;
    @BindView(R.id.profileAvi)
    ImageView profileAvi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.mobile_ads_APP_ID));
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuthService.getFirebaseAuthInstance();
        twitterSignoutButton.setOnClickListener(this);
        profileAvi.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService statusDetail = twitterApiClient.getAccountService();
        Call<com.twitter.sdk.android.core.models.User> call = statusDetail.verifyCredentials(true, true, true);
        call.enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
            @Override
            public void success(Result<com.twitter.sdk.android.core.models.User> result) {
                FirebaseFirestore fsDB = firestoreService.getFsDB();
                DocumentReference docRef = fsDB.collection("users").document(result.data.idStr);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        Picasso.get().load(user.getAviUrl())
                                .resize(MAX_WIDTH, MAX_HEIGHT)
                                .centerCrop()
                                .transform(new CircleTransform())
                                .into(profileAvi);
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                FirebaseCrash.logcat(Log.ERROR, TAG + ":TwitterException:failure", exception.getMessage());
            }
        });
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
        } else if (v == profileAvi) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.your_placeholder, new UserProfileFragment(), "UserProfileFrag")
                    .commit();
        }
    }
}
