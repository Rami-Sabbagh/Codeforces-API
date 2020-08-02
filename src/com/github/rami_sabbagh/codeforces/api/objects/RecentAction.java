package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents a recent action.
 */
public class RecentAction extends CFObject {

    /**
     * Action time, in unix format.
     */
    public long timeSeconds;

    /**
     * BlogEntry object in short form. Can be absent.
     */
    BlogEntry blogEntry;

    /**
     * Comment object. Can be absent.
     */
    Comment comment;
}
