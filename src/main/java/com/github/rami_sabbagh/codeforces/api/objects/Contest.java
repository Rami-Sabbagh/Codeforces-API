package com.github.rami_sabbagh.codeforces.api.objects;

import com.github.rami_sabbagh.codeforces.api.enums.ContestPhase;
import com.github.rami_sabbagh.codeforces.api.enums.ContestType;

import java.net.URI;

/**
 * Represents a contest on Codeforces.
 */
public class Contest extends CFObject {

    public int id;

    /**
     * Localized.
     */
    public String name;

    /**
     * CF, IOI, ICPC. Scoring system used for the contest.
     */
    public ContestType type;

    /**
     * CF, IOI, ICPC. Scoring system used for the contest.
     */
    public ContestPhase phase;

    /**
     * If true, then the ranklist for the contest is frozen and shows only submissions, created before freeze.
     */
    public boolean frozen;

    /**
     * Duration of the contest in seconds.
     */
    public long durationSeconds;

    /**
     * Can be absent. Contest start time in unix format.
     */
    public long startTimeSeconds;

    /**
     * Can be absent. Number of seconds, passed after the start of the contest. Can be negative.
     */
    public long relativeTimeSeconds;

    /**
     * Can be absent. Handle of the user, how created the contest.
     */
    public String preparedBy;

    /**
     * Can be absent. URL for contest-related website.
     */
    public URI websiteUrl;

    /**
     * Localized. Can be absent.
     */
    public String description;

    /**
     * Can be absent. From 1 to 5. Larger number means more difficult problems.
     */
    public int difficulty;

    /**
     * Localized. Can be absent. Human-readable type of the contest from the following categories:
     * Official ICPC Contest, Official School Contest, Opencup Contest, School/University/City/Region Championship,
     * Training Camp Contest, Official International Personal Contest, Training Contest.
     */
    public String kind;

    /**
     * Localized. Can be absent. Name of the Region for official ICPC contests.
     */
    public String icpcRegion;

    /**
     * Localized. Can be absent.
     */
    public String country;

    /**
     * Localized. Can be absent.
     */
    public String city;

    /**
     * Can be absent.
     */
    public String season;
}
