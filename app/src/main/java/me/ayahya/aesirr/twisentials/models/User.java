package me.ayahya.aesirr.twisentials.models;

import com.google.firebase.firestore.ServerTimestamp;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class User {
    String aviUrl;
    String bannerUrl;
    String createdAt;
    String description;
    String email;
    String lang;
    String name;
    String twitterId;
    int followersCount;
    int friendsCount;
    int favoritesCount;
    Date serverTimestamp;

    public User() { } // Needed for Firebase

    public User(String aviUrl, String bannerUrl, String createdAt, String description,
                String email, String lang, String name, String twitterId,
                int followersCount, int friendsCount, int favoritesCount)
    {
        this.aviUrl = aviUrl;
        this.bannerUrl = bannerUrl;
        this.createdAt = createdAt;
        this.description = description;
        this.email = email;
        this.lang = lang;
        this.name = name;
        this.twitterId = twitterId;
        this.followersCount = followersCount;
        this.friendsCount = friendsCount;
        this.favoritesCount = favoritesCount;
    }

    public String getAviUrl() { return aviUrl; }

    public String getBannerUrl() { return bannerUrl; }

    public String getCreatedAt() { return createdAt; }

    public String getDescription() { return description; }

    public String getEmail() { return email; }

    public String getLang() { return lang; }

    public String getName() { return name; }

    public String getTwitterId() { return twitterId; }

    public int getFollowersCount() { return followersCount; }

    public int getFriendsCount() { return friendsCount; }

    public int getFavoritesCount() { return favoritesCount; }

    @ServerTimestamp
    public Date getServerTimestamp() { return serverTimestamp; }
    private void setServerTimestamp(Date serverTimestamp) { this.serverTimestamp = serverTimestamp; }
}