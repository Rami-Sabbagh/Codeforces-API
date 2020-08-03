package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents a Codeforces blog entry, May be in either short or full version.
 */
public class BlogEntry extends CFObject {

    public int id;

    /**
     * Original locale of the blog entry.
     */
    public String originalLocale;

    /**
     * Time, when blog entry was created, in unix format.
     */
    public long creationTimeSeconds;

    /**
     * Author user handle.
     */
    public String authorHandle;

    /**
     * Localized.
     */
    public String title;

    /**
     * Localized. Not included in short version.
     */
    public String content;

    public String locale;

    /**
     * Time, when blog entry has been updated, in unix format.
     */
    public long modificationTimeSeconds;

    /**
     * If true, you can view any specific revision of the blog entry.
     */
    public boolean allowViewHistory;

    public String[] tags;

    public int rating;
}
