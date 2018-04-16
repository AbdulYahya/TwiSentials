package me.ayahya.aesirr.twisentials.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.twitter.sdk.android.core.Twitter;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.models.User;
import me.ayahya.aesirr.twisentials.services.FirebaseAuthService;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import me.ayahya.aesirr.twisentials.services.TwitterService;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private FirestoreService firestoreService = new FirestoreService();
    private TwitterService twitterService = new TwitterService();

    private User user;
    private FirebaseAuth firebaseAuth;
    private SharedPrefs sharedPrefs;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        super.addContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPrefs = SharedPrefs.newInstance(this);
        firebaseAuth = firebaseAuthService.getFirebaseAuthInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        TextView followersCount = findViewById(R.id.followers_count);
        TextView friendsCount = findViewById(R.id.friends_count);

        followersCount.setTextColor(Color.parseColor(sharedPrefs.getFollowersColor()));
        friendsCount.setTextColor(Color.parseColor(sharedPrefs.getFriendsColor()));
        followersCount.setText(sharedPrefs.getFollowersCount());
        friendsCount.setText(sharedPrefs.getFriendsCount());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firestoreService.getFsDB().collection("users").document(sharedPrefs.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        TextView followersCount = findViewById(R.id.followers_count);
                        TextView friendsCount = findViewById(R.id.friends_count);
                        int pFollowerCount = Integer.parseInt(sharedPrefs.getFollowersCount());
                        int pFriendCount = Integer.parseInt(sharedPrefs.getFriendsCount());
                        user = snapshot.toObject(User.class);
                        int cFollowerCount = Integer.parseInt(user.getFollowers().get("count").toString());
                        int cFriendCount = Integer.parseInt(user.getFriends().get("count").toString());

                        if (pFollowerCount < cFollowerCount) {
                            followersCount.setTextColor(Color.parseColor("green"));
                        } else if (pFollowerCount > cFollowerCount) {
                            followersCount.setTextColor(Color.parseColor("red"));
                        } else {
                            sharedPrefs.setFollowersColor("#9e9e9e");
                        }

                        if (pFriendCount < cFriendCount) {
                            sharedPrefs.setFriendsColor("green");
                        } else if (pFriendCount > cFriendCount) {
                            sharedPrefs.setFriendsColor("red");
                        } else {
                            sharedPrefs.setFriendsColor("#9e9e9e");
                        }

                        sharedPrefs.setFollowersCount(String.valueOf(cFollowerCount));
                        sharedPrefs.setFriendsCount(String.valueOf(cFriendCount));
                        followersCount.setText(sharedPrefs.getFollowersCount());
                        friendsCount.setText(sharedPrefs.getFriendsCount());
                    }

                });
            }
        });

//        firestoreService.getFsDB()
//                .collection("users")
//                .document(sharedPrefs.getUserId())
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot snapshot) {
//                        user = snapshot.toObject(User.class);
//                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                            @Override
//                            public void onRefresh() {
//                                int pFollowerCount = Integer.parseInt(sharedPrefs.getFollowersCount());
//                                int pFriendCount = Integer.parseInt(sharedPrefs.getFriendsCount());
//                                int cFollowerCount = Integer.parseInt(user.getFollowers().get("count").toString());
//                                int cFriendCount = Integer.parseInt(user.getFriends().get("count").toString());
//                                sharedPrefs.setFollowersCount(String.valueOf(cFollowerCount));
//                                sharedPrefs.setFriendsCount(String.valueOf(cFriendCount));
//
//                                if (pFollowerCount < cFollowerCount) {
//                                    sharedPrefs.setFollowersColor("green");
//                                } else if (pFollowerCount > cFollowerCount) {
//                                    sharedPrefs.setFollowersColor("red");
//                                } else {
//                                    sharedPrefs.setFollowersColor("#9e9e9e");
//                                }
//
//                                if (pFriendCount < cFriendCount) {
//                                    sharedPrefs.setFriendsColor("green");
//                                } else if (pFriendCount > cFriendCount) {
//                                    sharedPrefs.setFriendsColor("red");
//                                } else {
//                                    sharedPrefs.setFriendsColor("#9e9e9e");
//                                }
//
//                                followersCount
//                                        .setTextColor(Color.parseColor(sharedPrefs.getFollowersColor()));
//                                friendsCount
//                                        .setTextColor(Color.parseColor(sharedPrefs.getFriendsColor()));
//                                followersCount
//                                        .setText(sharedPrefs.getFollowersCount());
//                                friendsCount
//                                        .setText(sharedPrefs.getFriendsCount());
//                            }
//                        });
//                    }
//                });



    }
}
