package me.ayahya.aesirr.twisentials.services;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.twitter.sdk.android.core.Result;

import java.util.HashMap;

import me.ayahya.aesirr.twisentials.models.User;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;

public class FirestoreService {
    private TwitterService twitterService;
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private HashMap<String, Object> followers = new HashMap<>();
    private HashMap<String, Object> friends = new HashMap<>();

    private SharedPrefs sharedPrefs;
//    private User currentUser;

    public FirebaseFirestore getFsDB() { return fsDB; }

    public void newUserDocument(User newUser) {
        fsDB.collection("users").document(newUser.getTwitterId()).set(newUser);
    }

    public void userRatio(Context context) {
        twitterService = TwitterService.newInstance(context.getApplicationContext());

        Result<com.twitter.sdk.android.core.models.User> userResult = twitterService.currentUser();

        // This method is only used to store new users
        // move this logic over into a method in the Firestore service
        // note: have a onChange listener wrap over the method
        int currentFollowerCount = userResult.data.followersCount;

        // Add current follower count to hashmap
//        followers.put("count", String.valueOf(currentFollowerCount));

        if (sharedPrefs.getFollowersCount() != null) {
            int prevFollowerCount = Integer.parseInt(sharedPrefs.getFollowersCount());

            if (prevFollowerCount < currentFollowerCount) {
                followers.put("color", "green");
            } else if (prevFollowerCount > currentFollowerCount) {
                followers.put("color", "red");
            } else {
                followers.put("color", "#9e9e9e");
            }
        } else {
            followers.put("color", "#9e9e9e");
        }
//
//        friends.put("color", "#9e9e9e");
//        friends.put("count", String.valueOf(userResult.data.friendsCount));
    }

    public void trackRatio(Context context) { // Maybe add sharedPrefs?
        sharedPrefs = SharedPrefs.newInstance(context);
        final DocumentReference docRef =
                fsDB.collection("users").document(sharedPrefs.getUserId());

        fsDB.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            User currentUser = snapshot.toObject(User.class);

            Integer prevFollowerCount = Integer.parseInt(sharedPrefs.getFollowersCount());
            Integer currentFollowerCount = Integer.parseInt(currentUser.getFollowers().get("count").toString());

            Integer prevFriendCount = Integer.parseInt(sharedPrefs.getFriendsCount());
            Integer currentFriendCount = Integer.parseInt(currentUser.getFriends().get("count").toString());

            if (!prevFollowerCount.equals(currentFollowerCount)) {
                if (prevFollowerCount < currentFollowerCount) {
                    transaction.update(docRef, "followers.color", "green");
                    sharedPrefs.setFollowersColor("green");
                } else if (prevFollowerCount > currentFollowerCount) {
                    transaction.update(docRef, "followers.color", "red");
                    sharedPrefs.setFollowersColor("red");
                }
            }

//            transaction.update(docRef, "followers.color", "#9e9e9e");
//            sharedPrefs.setFollowersColor("#9e9e9e");

            if (!prevFriendCount.equals(currentFriendCount)) {
                if (prevFriendCount < currentFriendCount) {
                    transaction.update(docRef, "friends.color", "green");
                    sharedPrefs.setFriendsColor("green");
                } else if (prevFriendCount > currentFriendCount) {
                    transaction.update(docRef, "friends.color", "red");
                    sharedPrefs.setFriendsColor("red");
                }
            }

//            transaction.update(docRef, "friends.color", "#9e9e9e");
//            sharedPrefs.setFriendsColor("#9e9e9e");

            return null;
        })
        .addOnSuccessListener(SuccessMsg -> Log.e("TrackRatio", "Transaction success: " + SuccessMsg))
        .addOnFailureListener(FailureMsg -> Log.e("TrackRatio", "Transaction failure.", FailureMsg));
    }

    public void ratioOnChangeListener(TextView textView) {
        DocumentReference documentRef = fsDB.collection("users").document(sharedPrefs.getUserId());

        documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException firestoreException) {
                if (firestoreException != null) {
                    Log.w("ratioOnChangeListener", "Listener failed...", firestoreException);
                    return;
                }

                // Determine the source of events received by your snapshot listener
                // Local writes in your app will invoke snapshot listeners immediately.
                // This is because of an important feature called "latency compensation."
                // When you perform a write, your listeners will be notified with the new data
                // before the data is sent to the backend.
                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d("ratioOnChangeListener", "Current data: " + snapshot.getData());
                    textChangeListener(textView);
                } else {
                    Log.d("ratioOnChangeListener", "Current data: null");
                }
            }
        });
    }

    private void textChangeListener(TextView textView) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // try on string changes -- sharedPrefs
                textView
                        .setTextColor(Color.parseColor(sharedPrefs.getFollowersColor()));
                textView
                        .setText(sharedPrefs.getFollowersCount());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

}
