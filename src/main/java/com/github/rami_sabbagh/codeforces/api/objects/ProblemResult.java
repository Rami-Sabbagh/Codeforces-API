package com.github.rami_sabbagh.codeforces.api.objects;

import com.github.rami_sabbagh.codeforces.api.enums.ProblemResultType;

/**
 * Represents a submission results of a party for a problem.
 */
public class ProblemResult extends CFObject {

    public double points;

    /**
     * Penalty (in ICPC meaning) of the party for this problem.
     */
    public int penalty;

    /**
     * Number of incorrect submissions.
     */
    public int rejectedAttemptCount;

    /**
     * If type is PRELIMINARY then points can decrease (if, for example, solution will fail during system test).
     * Otherwise, party can only increase points for this problem by submitting better solutions.
     */
    public ProblemResultType type;

    /**
     * Number of seconds after the start of the contest before the submission,
     * that brought maximal amount of points for this problem.
     */
    public long bestSubmissionTimeSeconds;
}
