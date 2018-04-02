package me.ayahya.aesirr.twisentials.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.utils.CircleTransform;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirestoreService firestoreService = new FirestoreService();

    private SharedPrefs sharedPrefs;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                            TextView followersCount = findViewById(R.id.followers_count);
                            TextView friendsCount = findViewById(R.id.friends_count);

                            me.ayahya.aesirr.twisentials.models.User currentUser =
                                    documentSnapshot.toObject(me.ayahya.aesirr.twisentials.models.User.class);

                            // Set shared Preferences
                            setSharedPrefs(currentUser.getAviUrl(), currentUser.getBannerUrl(), currentUser.getName(),
                                    currentUser.getEmail(), String.valueOf(currentUser.getFavoritesCount()),
                                    String.valueOf(currentUser.getFollowersCount()), String.valueOf(currentUser.getFriendsCount()));
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




    private void setSharedPrefs(String userAvi, String userBanner, String userName,
                                String userEmail, String favoritesCount, String followersCount, String friendsCount ) {
        sharedPrefs.setImagePath("userAvi", userAvi);
        sharedPrefs.setImagePath("userBanner", userBanner);
        sharedPrefs.setUserName(userName);
        sharedPrefs.setUserEmail(userEmail);
        sharedPrefs.setFavoritesCount(favoritesCount);
        sharedPrefs.setFollowersCount(followersCount);
        sharedPrefs.setFriendsCount(friendsCount);
    }
}
