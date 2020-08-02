package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents a ranklist row.
 */
public class RanklistRow extends CFObject {

    /**
     * Party that took a corresponding place in the contest.
     */
    public Party party;

    /**
     * Party place in the contest.
     */
    public int rank;

    /**
     * Total amount of points, scored by the party.
     */
    public double points;

    /**
     * Total penalty (in ICPC meaning) of the party.
     */
    public int penalty;

    public int successfulHackCount;

    public int unsuccessfulHackCount;

    /**
     * Party results for each problem.
     * Order of the problems is the same as in "problems" field of the returned object.
     */
    public ProblemResult[] problemResults;

    /**
     * For IOI contests only.
     * Time in seconds from the start of the contest to the last submission that added some points
     * to the total score of the party.
     */
    public long lastSubmissionTimeSeconds;

}
