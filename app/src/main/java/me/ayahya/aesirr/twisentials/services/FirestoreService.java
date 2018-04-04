package me.ayahya.aesirr.twisentials.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import me.ayahya.aesirr.twisentials.models.User;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;

public class FirestoreService {
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private SharedPrefs sharedPrefs;
    public FirebaseFirestore getFsDB() { return fsDB; }

    public void newUserDocument(User newUser) {
        fsDB.collection("users").document(newUser.getTwitterId()).set(newUser);
    }

    public void trackRatio(Context context) {
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

            if (prevFollowerCount < currentFollowerCount) {
                transaction.update(docRef, "followers.color", "green");
            } else if (prevFollowerCount > currentFollowerCount) {
                transaction.update(docRef, "followers.color", "red");
            } else {
                transaction.update(docRef, "followers.color", "#9e9e9e");
            }

            if (prevFriendCount < currentFriendCount) {
                transaction.update(docRef, "friends.color", "green");
            } else if (prevFriendCount > currentFriendCount) {
                transaction.update(docRef, "friends.color", "red");
            } else {
                transaction.update(docRef, "friends.color", "#9e9e9e");
            }

            return null;
        })
        .addOnSuccessListener(SuccessMsg -> Log.e("TrackRatio", "Transaction success: " + SuccessMsg))
        .addOnFailureListener(FailureMsg -> Log.e("TrackRatio", "Transaction failure.", FailureMsg));
    }

}
