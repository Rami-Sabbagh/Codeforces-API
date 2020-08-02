package com.github.rami_sabbagh.codeforces.api.objects;

/**
 * Represents a party, participating in a contest.
 */
@SuppressWarnings("unused")
public class Party {

    /**
     * Can be absent. Id of the contest, in which party is participating.
     */
    public int contestId;

    /**
     * Members of the party.
     */
    public Member[] members;

    public ParticipantType participantType;

    /**
     * Can be absent. If party is a team, then it is a unique team id. Otherwise, this field is absent.
     */
    public int teamId;

    /**
     * Localized. Can be absent. If party is a team or ghost, then it is a localized name of the team. Otherwise, it is absent.
     */
    public String teamName;

    /**
     * If true then this party is a ghost. It participated in the contest, but not on Codeforces.
     * For example, Andrew Stankevich Contests in Gym has ghosts of the participants from Petrozavodsk Training Camp.
     */
    public boolean ghost;

    /**
     * Can be absent. Room of the party. If absent, then the party has no room.
     */
    public int room;

    /**
     * Can be absent. Time, when this party started a contest.
     */
    public long startTimeSeconds;

    /**
     * Party's participantType's enum.
     */
    public enum ParticipantType {
        CONTESTANT,
        PRACTICE,
        VIRTUAL,
        MANAGER,
        OUT_OF_COMPETITION
    }
}
