package me.ayahya.aesirr.twisentials.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.services.AccountService;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ayahya.aesirr.twisentials.R;
import me.ayahya.aesirr.twisentials.models.User;
import me.ayahya.aesirr.twisentials.services.FirestoreService;
import retrofit2.Call;

public class UserProfileFragment extends DialogFragment implements View.OnClickListener {
    @BindView(R.id.twitter_handle)
    TextView twitterHandle;
    @BindView(R.id.friendsCount)
    TextView friendsCount;
    @BindView(R.id.followersCount)
    TextView followersCount;
    @BindView(R.id.close_frag)
    TextView closeFrag;

    private FirestoreService firestoreService = new FirestoreService();
    private User user;

    public UserProfileFragment() { } // required

    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        userProfileFragment.setArguments(args);
        return userProfileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (user != null) {
            user = Parcels.unwrap(getArguments().getParcelable("user"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, view);

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
                        me.ayahya.aesirr.twisentials.models.User user = documentSnapshot.toObject(me.ayahya.aesirr.twisentials.models.User.class);
                        twitterHandle.setText(user.getName());
                        friendsCount.setText(String.valueOf(user.getFriendsCount()));
                        followersCount.setText(String.valueOf(user.getFollowersCount()));
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
//                FirebaseCrash.logcat(Log.ERROR, TAG + ":TwitterException:failure", exception.getMessage());
            }
        });

        closeFrag.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == closeFrag) {
            dismiss();
        }
    }

}
