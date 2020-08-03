package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents a comment.
 */
public class Comment extends CFObject {

    public int id;

    /**
     * Time, when comment was created, in unix format.
     */
    public long creationTimeSeconds;

    public String commentatorHandle;

    public String locale;

    public String text;

    /**
     * Can be absent.
     */
    public int parentCommentId;

    public int rating;
}
