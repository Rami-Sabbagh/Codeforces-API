package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents a statistic data about a problem.
 */
public class ProblemStatistics extends CFObject {
    /**
     * Can be absent. Id of the contest, containing the problem.
     */
    public int contestId;

    /**
     * Usually a letter of a letter, followed by a digit, that represent a problem index in a contest.
     */
    public String index;

    /**
     * Number of users, who solved the problem.
     */
    public int solvedCount;
}
