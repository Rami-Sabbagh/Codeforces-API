package com.github.rami_sabbagh.codeforces.api.objects;

import com.github.rami_sabbagh.codeforces.api.enums.Testset;
import com.github.rami_sabbagh.codeforces.api.enums.Verdict;

/**
 * Represents a submission.
 */
@SuppressWarnings("unused")
public class Submission extends CFObject {

    public int id;

    /**
     * Can be absent.
     */
    public int contestId;

    /**
     * Time, when submission was created, in unix-format.
     */
    public long creationTimeSeconds;

    /**
     * Number of seconds, passed after the start of the contest (or a virtual start for virtual parties), before the submission.
     */
    public long relativeTimeSeconds;

    public Problem problem;

    public Party author;

    public String programmingLanguage;

    public Verdict verdict;

    /**
     * Testset used for judging the submission.
     */
    public Testset testset;

    /**
     * Number of passed tests.
     */
    public int passedTestCount;

    /**
     * Maximum time in milliseconds, consumed by solution for one test.
     */
    public int timeConsumedMillis;

    /**
     * Maximum memory in bytes, consumed by solution for one test.
     */
    public int memoryConsumedBytes;

    /**
     * Can be absent. Number of scored points for IOI-like contests.
     */
    public double points;

}
