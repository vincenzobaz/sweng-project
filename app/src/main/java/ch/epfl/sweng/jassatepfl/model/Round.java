package ch.epfl.sweng.jassatepfl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.jassatepfl.model.Match.Meld;

/**
 * Class representing a round object. It contains the points and melds obtained in the round.
 */
public class Round {

    private int teamCount;
    private Map<String, Integer> scores;
    private Map<String, List<Meld>> melds;
    private Map<String, Integer> meldScores;

    public Round() {
    }

    public Round(int teamCount) {
        if (teamCount <= 0) {
            throw new IllegalArgumentException("Invalid number of teams");
        }
        this.teamCount = teamCount;
        this.scores = new HashMap<>();
        this.melds = new HashMap<>();
        this.meldScores = new HashMap<>();

        for (int i = 0; i < teamCount; ++i) {
            String key = concatKey(i);
            scores.put(key, 0);
            melds.put(key, new ArrayList<Meld>());
            meldScores.put(key, 0);
        }
    }

    public int getTeamCount() {
        return teamCount;
    }

    public Map<String, Integer> getScores() {
        return Collections.unmodifiableMap(scores);
    }

    public Map<String, List<Meld>> getMelds() {
        return Collections.unmodifiableMap(melds);
    }

    public Map<String, Integer> getMeldScores() {
        return Collections.unmodifiableMap(meldScores);
    }

    /**
     * Returns the points of the specified team, excluding points obtained
     * with melds.
     *
     * @param teamIndex the index of the team
     * @return the points of the team for this round
     */
    public Integer getTeamCardScore(int teamIndex) {
        if (teamIndex < 0 || teamIndex >= teamCount) {
            throw new IndexOutOfBoundsException("Invalid team index");
        }
        return scores.get(concatKey(teamIndex));
    }

    /**
     * Returns the points obtained with melds for the specified team.
     *
     * @param teamIndex the index of the team
     * @return the meld points of the team
     */
    public Integer getTeamMeldScore(int teamIndex) {
        if (teamIndex < 0 || teamIndex >= teamCount) {
            throw new IndexOutOfBoundsException("Invalid team index");
        }
        return meldScores.get(concatKey(teamIndex));
    }

    /**
     * Returns the total points obtained by the given team for this round
     * (melds and card points).
     *
     * @param teamIndex the index of the team
     * @return the total points for this round
     */
    public Integer getTeamTotalScore(int teamIndex) {
        if (teamIndex < 0 || teamIndex >= teamCount) {
            throw new IndexOutOfBoundsException("Invalid team index");
        }
        return getTeamCardScore(teamIndex) + getTeamMeldScore(teamIndex);
    }

    /**
     * Returns the melds obtained by the specified team for this round.
     *
     * @param teamIndex the index of the team
     * @return the melds
     */
    public List<Meld> getTeamMelds(int teamIndex) {
        if (teamIndex < 0 || teamIndex >= teamCount) {
            throw new IndexOutOfBoundsException("Invalid team index");
        }
        return Collections.unmodifiableList(melds.get(concatKey(teamIndex)));
    }

    /**
     * Sets the score of the given team.
     *
     * @param teamIndex the index of the team
     * @param score     the score of the team
     */
    public void setTeamScore(int teamIndex, int score) {
        if (teamIndex < 0 || teamIndex >= teamCount) {
            throw new IndexOutOfBoundsException("Invalid team index");
        }
        scores.put(concatKey(teamIndex), score);
    }

    /**
     * Adds the given meld to the current meld list and adds its point value
     * to the meld score.
     *
     * @param teamIndex the index of the team that got the meld
     * @param meld      the meld
     */
    public void addMeldToTeam(int teamIndex, Meld meld) {
        if (teamIndex < 0 || teamIndex >= teamCount) {
            throw new IndexOutOfBoundsException("Invalid team index");
        }

        String key = concatKey(teamIndex);
        melds.get(key).add(meld);
        Integer tmp = meldScores.get(key);
        meldScores.put(key, tmp + meld.value());
    }

    private String concatKey(int index) {
        return "TEAM" + index;
    }

}
