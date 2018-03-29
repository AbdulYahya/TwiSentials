package me.ayahya.aesirr.twisentials.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.CircleTransform;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import retrofit2.Call;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int MAX_HEIGHT = 400;
    private static final int MAX_WIDTH = 400;
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirestoreService firestoreService = new FirestoreService();
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.profileBanner)
    ImageView profileBanner;
    @BindView(R.id.profileAvi)
    ImageView profileAvi;
    @BindView(R.id.twitter_handle)
    TextView twitterHandle;
    @BindView(R.id.followersCount)
    TextView followersCount;
    @BindView(R.id.friendsCount)
    TextView friendsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuthService.getFirebaseAuthInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService statusDetail = twitterApiClient.getAccountService();
        Call<User> call = statusDetail.verifyCredentials(true, true, true);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                FirebaseFirestore fsDB = firestoreService.getFsDB();
                DocumentReference docRef = fsDB.collection("users").document(result.data.idStr);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        me.ayahya.aesirr.twisentials.models.User user = documentSnapshot.toObject(me.ayahya.aesirr.twisentials.models.User.class);
                        Picasso.get().load(user.getAviUrl())
                                .resize(MAX_WIDTH, MAX_HEIGHT)
                                .centerCrop()
                                .transform(new CircleTransform())
                                .into(profileAvi);
                        Picasso.get().load(user.getBannerUrl())
                                .into(profileBanner);
                        twitterHandle.setText(user.getName());
                        friendsCount.setText(String.valueOf(user.getFriendsCount()));
                        followersCount.setText(String.valueOf(user.getFollowersCount()));

                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                FirebaseCrash.logcat(Log.ERROR, TAG + ":TwitterException:failure", exception.getMessage());
            }
        });
    }
}
