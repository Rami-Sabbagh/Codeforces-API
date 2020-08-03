package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents a participation of user in rated contest.
 */
public class RatingChange extends CFObject {

    public int contestId;

    /**
     * Localized.
     */
    public String contestName;

    /**
     * Codeforces user handle.
     */
    public String handle;

    /**
     * Place of the user in the contest.
     * This field contains user rank on the moment of rating update.
     * If afterwards rank changes (e.g. someone get disqualified),
     * this field will not be update and will contain old rank.
     */
    public int rank;

    /**
     * Time, when rating for the contest was update, in unix-format.
     */
    public int ratingUpdateTimeSeconds;

    /**
     * User rating before the contest.
     */
    public int oldRating;

    /**
     * User rating after the contest.
     */
    public int newRating;
}
