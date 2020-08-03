package com.github.rami_sabbagh.codeforces.api.objects;

import com.github.rami_sabbagh.codeforces.api.enums.HackVerdict;

/**
 * Represents a hack, made during Codeforces Round.
 */
public class Hack extends CFObject {

    public int id;

    /**
     * Hack creation time in unix format.
     */
    public long creationTimeSeconds;

    public Party hacker;

    public Party defender;

    /**
     * Can be absent.
     */
    public HackVerdict verdict;

    /**
     * Hacked problem.
     */
    public Problem problem;

    /**
     * Can be absent.
     */
    public String test;

    /**
     * Localized. Can be absent.
     */
    public JudgeProtocol judgeProtocol;

    /**
     * Represents the judging protocol for a hack.
     */
    public static class JudgeProtocol extends CFObject {

        /**
         * If manual is <i>true</i> then test for the hack was entered manually.
         */
        public boolean manual;

        /**
         * Human-readable description of judge protocol.
         */
        public String protocol;

        /**
         * Human-readable description of hack verdict.
         */
        public String verdict;
    }
}
