package me.ayahya.aesirr.twisentials.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import me.ayahya.aesirr.twisentials.models.User;

public class FirestoreService {
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();

    public FirebaseFirestore getFsDB() { return fsDB; }

    public void newUserDocument(User newUser) {
        fsDB.collection("users").document(newUser.getTwitterId()).set(newUser);
        // Twitter id for the document id
    }

}
