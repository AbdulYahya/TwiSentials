package me.ayahya.aesirr.twisentials.models;

import com.google.firebase.firestore.ServerTimestamp;

import org.parceler.Parcel;

import java.util.Date;
import java.util.HashMap;

@Parcel
public class User {
    String aviUrl;
    String bannerUrl;
    String createdAt;
    String description;
    String email;
    String lang;
    String name;
    String screenName;
    String twitterId;
    int favoritesCount;

    HashMap<String, Object> followers = new HashMap<>();
    HashMap<String, Object> friends = new HashMap<>();
    Date serverTimestamp;

    public User() { } // Needed for Firebase

    public User(String aviUrl, String bannerUrl, String createdAt, String description,
                String email, String lang, String name, String screenName, String twitterId,
                HashMap<String, Object> followers, HashMap<String, Object> friends, int favoritesCount)
    {
        this.aviUrl = aviUrl;
        this.bannerUrl = bannerUrl;
        this.createdAt = createdAt;
        this.description = description;
        this.email = email;
        this.lang = lang;
        this.name = name;
        this.screenName = screenName;
        this.twitterId = twitterId;
        this.followers = followers;
        this.friends = friends;
        this.favoritesCount = favoritesCount;
    }

    public String getAviUrl() { return aviUrl; }

    public String getBannerUrl() { return bannerUrl; }

    public String getCreatedAt() { return createdAt; }

    public String getDescription() { return description; }

    public String getEmail() { return email; }

    public HashMap<String, Object> getFollowers() { return followers; }

    public HashMap<String, Object> getFriends() { return friends; }

    public String getLang() { return lang; }

    public String getName() { return name; }

    public String getScreenName() { return screenName; }

    public String getTwitterId() { return twitterId; }

    public int getFavoritesCount() { return favoritesCount; }

    @ServerTimestamp
    public Date getServerTimestamp() { return serverTimestamp; }
    private void setServerTimestamp(Date serverTimestamp) { this.serverTimestamp = serverTimestamp; }
}