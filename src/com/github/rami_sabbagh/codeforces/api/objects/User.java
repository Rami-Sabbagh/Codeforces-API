package com.github.rami_sabbagh.codeforces.api.objects;

import java.net.URI;

/**
 * Represents a Codeforces user.
 */
@SuppressWarnings("unused")
public class User {
    /**
     * Codeforces user handle.
     */
    public String handle;

    /**
     * Shown only if user allowed to share his contact info.
     */
    public String email;

    /**
     * User id for VK social network. Shown only if user allowed to share his contact info.
     */
    public String vkld;

    /**
     * Shown only if user allowed to share his contact info.
     */
    public String openId;

    /**
     * Localized. Can be absent.
     */
    public String firstName;

    /**
     * Localized. Can be absent.
     */
    public String lastName;

    /**
     * Localized. Can be absent.
     */
    public String country;

    /**
     * Localized. Can be absent.
     */
    public String city;

    /**
     * Localized. Can be absent.
     */
    public String organization;

    /**
     * User contribution.
     */
    public int contribution;

    /**
     * Localized.
     */
    public String rank;

    public int rating;

    /**
     * Localized.
     */
    public String maxRank;

    public int maxRating;

    /**
     * Time, when user was last seen online, in unix format.
     */
    public long lastOnlineTimeSeconds;

    /**
     * Time, when user was registered, in unix format.
     */
    public long registrationTimeSeconds;

    /**
     * Amount of users who have this user in friends.
     */
    public int friendOfCount;

    /**
     * User's avatar URL.
     */
    public URI avatar;

    /**
     * User's title photo URL.
     */
    public URI titlePhoto;
}
