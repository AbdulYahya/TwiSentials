package me.ayahya.aesirr.twisentials.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
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
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import me.ayahya.aesirr.twisentials.utils.CircleTransform;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;
import retrofit2.Call;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.content_frame) @Nullable FrameLayout contentFrame;

    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirestoreService firestoreService = new FirestoreService();
    private FirebaseAuth firebaseAuth;
    private SharedPrefs sharedPrefs;

    private static final int AVI_MAX_WIDTH = 192;
    private static final int AVI_MAX_HEIGHT = 192;
    private static final int BANNER_MAX_WIDTH = 400;
    private static final int BANNER_MAX_HEIGHT = 400;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        init();
        firebaseAuth = firebaseAuthService.getFirebaseAuthInstance();
        sharedPrefs = SharedPrefs.newInstance(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuthService.getFirebaseAuthInstance().getCurrentUser();
        if (currentUser == null) {
            firebaseAuthService.completeSignout();
            Intent authIntent = new Intent(getApplicationContext(), AuthActivity.class);
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
                            ImageView userAvi = findViewById(R.id.nav_drawer_user_profile_avi);
                            ImageView userBanner = findViewById(R.id.nav_drawer_user_pic_cover);
                            TextView userDisplayName = findViewById(R.id.nav_drawer_user_display_name);
                            TextView userEmail = findViewById(R.id.nav_drawer_user_email);
                            TextView followersCount = findViewById(R.id.followers_count);
                            TextView friendsCount = findViewById(R.id.friends_count);


                            me.ayahya.aesirr.twisentials.models.User user = documentSnapshot
                                    .toObject(me.ayahya.aesirr.twisentials.models.User.class);

                            setSharedPrefs(user.getAviUrl(), user.getBannerUrl(), user.getTwitterId(),
                                    user.getName(), user.getEmail(), String.valueOf(user.getFavoritesCount()),
                                    String.valueOf(user.getFollowersCount()), String.valueOf(user.getFriendsCount()));

                            Picasso.with(getApplicationContext()).load(sharedPrefs.getImagePath("userAvi"))
                                    .resize(AVI_MAX_WIDTH, AVI_MAX_HEIGHT)
                                    .centerCrop()
                                    .transform(new CircleTransform())
                                    .into(userAvi);
                            Picasso.with(getApplicationContext()).load(sharedPrefs.getImagePath("userBanner"))
                                    .resize(BANNER_MAX_WIDTH, BANNER_MAX_HEIGHT)
                                    .centerCrop()
                                    .into(userBanner);
                            userDisplayName.setText(sharedPrefs.getUserName());
                            userEmail.setText(sharedPrefs.getUserEmail());
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                Intent home = new Intent(getApplicationContext(), MainActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(home);
                finish();
                break;
            case R.id.nav_timeline:
                Intent timeline = new Intent(getApplicationContext(), TimelineActivity.class);
                timeline.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(timeline);
                finish();
                break;
            case R.id.nav_slideshow:
                // Handle the slideshow action
                break;
            case R.id.nav_manage:
                // Handle the manage action
                break;
            case R.id.nav_share:
                // Handle the share action
                break;
            case R.id.nav_logout:
                firebaseAuthService.completeSignout();
                Intent authIntent = new Intent(BaseActivity.this, AuthActivity.class);
                authIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(authIntent);
                finish();

                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        NavigationView navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TweetComposer.Builder(BaseActivity.this)
                        .show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setSharedPrefs(String userAvi, String userBanner, String userId,
                                String userName, String userEmail, String favoritesCount,
                                String followersCount, String friendsCount ) {
        sharedPrefs.setImagePath("userAvi", userAvi);
        sharedPrefs.setImagePath("userBanner", userBanner);
        sharedPrefs.setUserId(userId);
        sharedPrefs.setUserName(userName);
        sharedPrefs.setUserEmail(userEmail);
        sharedPrefs.setFavoritesCount(favoritesCount);
        sharedPrefs.setFollowersCount(followersCount);
        sharedPrefs.setFriendsCount(friendsCount);
    }

    /**
     * called in extending activities instead of setContentView...
     *
     * @param layoutId The content Layout Id of extending activities
     */
    public void addContentView(int layoutId) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(layoutId, null, false);
        drawer.addView(contentView, 0);
    }
}
