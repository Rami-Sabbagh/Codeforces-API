package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents the standings of a contest on Codeforces.
 */
public class ContestStandings extends CFObject {

    public Contest contest;

    public Problem[] problems;

    public RanklistRow[] rows;

}
