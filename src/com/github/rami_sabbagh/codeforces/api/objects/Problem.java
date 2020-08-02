package com.github.rami_sabbagh.codeforces.api.objects;

import com.github.rami_sabbagh.codeforces.api.enums.ProblemType;

/**
 * Represents a problem.
 */
@SuppressWarnings("unused")
public class Problem {

    /**
     * Can be absent. Id of the contest, containing the problem.
     */
    public int contestId;

    /**
     * Can be absent. Short name of the problemset the problem belongs to.
     */
    public String problemsetName;

    /**
     * Usually a letter of a letter, followed by a digit, that represent a problem index in a contest.
     */
    public String index;

    /**
     * Localized.
     */
    public String name;

    public ProblemType type;

    /**
     * Can be absent. Maximum amount of points for the problem.
     */
    public double points;

    /**
     * Can be absent. Problem rating (difficulty).
     */
    public int rating;

    /**
     * Problem tags.
     */
    public String[] tags;
}
