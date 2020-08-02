package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents a submission.
 */
@SuppressWarnings("unused")
public class Submission {

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

    /**
     * Submission's verdict enum.
     */
    public enum Verdict {
        FAILED,
        OK,
        PARTIAL,
        COMPILATION_ERROR,
        RUNTIME_ERROR,
        WRONG_ANSWER,
        PRESENTATION_ERROR,
        TIME_LIMIT_EXCEEDED,
        MEMORY_LIMIT_EXCEEDED,
        IDLENESS_LIMIT_EXCEEDED,
        SECURITY_VIOLATED,
        CRASHED,
        INPUT_PREPARATION_CRASHED,
        CHALLENGED,
        SKIPPED,
        TESTING,
        REJECTED
    }

    /**
     * Submission's testset enum.
     */
    public enum Testset {
        SAMPLES,
        PRETESTS,
        TESTS,
        CHALLENGES,
        TESTS1,
        TESTS2,
        TESTS3,
        TESTS4,
        TESTS5,
        TESTS6,
        TESTS7,
        TESTS8,
        TESTS9,
        TESTS10
    }
}
