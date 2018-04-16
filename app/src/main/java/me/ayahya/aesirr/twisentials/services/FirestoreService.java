package me.ayahya.aesirr.twisentials.services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import me.ayahya.aesirr.twisentials.models.User;
import me.ayahya.aesirr.twisentials.utils.SharedPrefs;

public class FirestoreService {
    private static FirestoreService savedInstanceState;

//    private  TwitterService twitterService = new TwitterService();
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private SharedPrefs sharedPrefs;
//    private User currentUser;

    public static FirestoreService newInstance(Context context) {
        if (savedInstanceState == null) {
            savedInstanceState = new FirestoreService();
        }

        return savedInstanceState;
    }
    public FirebaseFirestore getFsDB() { return fsDB; }

    public void pushUserDocument(User newUser) {
        fsDB.collection("users").document(newUser.getTwitterId()).set(newUser);
    }

    public void trackRatio(Context context) { // Maybe add sharedPrefs?
        sharedPrefs = SharedPrefs.newInstance(context);
        final DocumentReference docRef =
                fsDB.collection("users").document(sharedPrefs.getUserId());

        fsDB.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            User currentUser = snapshot.toObject(User.class);

            Integer pFollowerCount = Integer.parseInt(sharedPrefs.getFollowersCount());
            Integer cFollowerCount = Integer.parseInt(currentUser.getFollowers().get("count").toString());

            Integer pFriendCount = Integer.parseInt(sharedPrefs.getFriendsCount());
            Integer cFriendCount = Integer.parseInt(currentUser.getFriends().get("count").toString());


            if (!pFollowerCount.equals(cFollowerCount)) {
                if (pFollowerCount < cFollowerCount) {
                    transaction.update(docRef, "followers.color", "green");
                    sharedPrefs.setFollowersColor("green");
                } else if (pFollowerCount > cFollowerCount) {
                    transaction.update(docRef, "followers.color", "red");
                    sharedPrefs.setFollowersColor("red");
                }
            }

            if (!pFriendCount.equals(cFriendCount)) {
                if (pFriendCount < cFriendCount) {
                    transaction.update(docRef, "friends.color", "green");
                    sharedPrefs.setFriendsColor("green");
                } else if (pFriendCount > cFriendCount) {
                    transaction.update(docRef, "friends.color", "red");
                    sharedPrefs.setFriendsColor("red");
                }
            }
            return null;
        })
        .addOnSuccessListener(SuccessMsg -> Log.e("TrackRatio", "Transaction success: " + SuccessMsg))
        .addOnFailureListener(FailureMsg -> Log.e("TrackRatio", "Transaction failure.", FailureMsg));
    }

//    public User getCurrentUser(String twitterId) {
//        fsDB.collection("users").document(twitterId)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot snapshot) {
//                        currentUser = snapshot.toObject(User.class);
//                        twitterService.setCurrentUser(currentUser);
//                        Log.e("FirestoreService", "onSuccess:Inside " + currentUser.getTwitterId());
//                    }
//                });
////                .addOnSuccessListener(snapshot ->
////                        currentUser = snapshot.toObject(User.class));
//        Log.e("FirestoreService", "TwitterService: " + twitterService.getCurrentUser());
//        return twitterService.getCurrentUser();
//    }
}
