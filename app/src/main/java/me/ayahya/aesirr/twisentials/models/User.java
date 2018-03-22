package me.ayahya.aesirr.twisentials.models;

public class User {
    private String
            aviUrl, bannerUrl, createdAt, description,
            email, lang, name, twitterId;
    private int
            followersCount, friendsCount;

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

    public void setAviUrl(String aviUrl) { this.aviUrl = aviUrl; }

    public String getBannerUrl() { return bannerUrl; }

    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getLang() { return lang; }

    public void setLang(String lang) { this.lang = lang; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getTwitterId() { return twitterId; }

    public void setTwitterId(String twitterId) { this.twitterId = twitterId; }

    public int getFollowersCount() { return followersCount; }

    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }

    public int getFriendsCount() { return friendsCount; }

    public void setFriendsCount(int friendsCount) { this.friendsCount = friendsCount; }
}