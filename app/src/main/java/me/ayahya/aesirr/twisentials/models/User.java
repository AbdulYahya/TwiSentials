package me.ayahya.aesirr.twisentials.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private String
            aviUrl, bannerUrl, createdAt, description,
            email, lang, name, twitterId;
    private int
            followersCount, friendsCount;
    private Date serverTimestamp;

    public User() { } // Needed for Firebase

    public User(String aviUrl, String bannerUrl, String createdAt, String description,
                String email, String lang, String name, String twitterId,
                int followersCount, int friendsCount)
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

    @ServerTimestamp
    public Date getServerTimestamp() { return serverTimestamp; }
    private void setServerTimestamp(Date serverTimestamp) { this.serverTimestamp = serverTimestamp; }
}