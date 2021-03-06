package ch.epfl.sweng.jassatepfl.stats;

import org.junit.Test;

import java.util.Map;

import ch.epfl.sweng.jassatepfl.model.Match;
import ch.epfl.sweng.jassatepfl.test_utils.DummyDataTest;

import static ch.epfl.sweng.jassatepfl.model.Match.Meld.FOUR_JACKS;
import static ch.epfl.sweng.jassatepfl.model.Match.Meld.THREE_CARDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for MatchStats class
 */
public class MatchStatsTest {

    private final Match match = DummyDataTest.fullMatch();

    @Test
    public void testTotalScoreGetter() {
        MatchStats stats = new MatchStats(match);
        stats.setMeld(0, THREE_CARDS);
        setTeamScores(stats, 57, 100);
        stats.setMeld(1, FOUR_JACKS);
        setTeamScores(stats, 33, 122);
        assertEquals(Integer.valueOf(110), stats.obtainTotalMatchScore(0));
        assertEquals(Integer.valueOf(422), stats.obtainTotalMatchScore(1));
    }

    @Test
    public void testCurrentRoundScoreGetter() {
        MatchStats stats = new MatchStats(match);
        stats.setScore(0, 89);
        stats.setMeld(0, THREE_CARDS);
        stats.setScore(1, 68);
        assertEquals(Integer.valueOf(109), stats.obtainCurrentRoundTeamScore(0));
        assertEquals(Integer.valueOf(68), stats.obtainCurrentRoundTeamScore(1));
    }

    @Test
    public void testPointsGoalGetter() {
        MatchStats stats = new MatchStats(match);
        stats.updatePointsGoal(0, 300);
        stats.updatePointsGoal(1, 500);
        Map<String, Integer> pointsGoal = stats.getPointsGoal();
        assertEquals(Integer.valueOf(300), pointsGoal.get("TEAM0"));
        assertEquals(Integer.valueOf(500), pointsGoal.get("TEAM1"));
    }

    @Test
    public void testCancelLastRound() {
        MatchStats stats = new MatchStats(match);
        setTeamScores(stats, 57, 100);
        setTeamScores(stats, 100, 57);
        assertEquals(Integer.valueOf(157), stats.obtainTotalMatchScore(0));
        assertEquals(Integer.valueOf(157), stats.obtainTotalMatchScore(1));
        stats.cancelLastRound(0);
        assertEquals(Integer.valueOf(57), stats.obtainTotalMatchScore(0));
        assertEquals(Integer.valueOf(100), stats.obtainTotalMatchScore(1));
    }

    @Test
    public void testMatchIsOver() {
        MatchStats stats = new MatchStats(match);
        assertFalse(stats.goalHasBeenReached());
        setTeamScores(stats, 500, 1000);
        assertTrue(stats.goalHasBeenReached());
        assertEquals(1, stats.getWinnerIndex());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRoundScoreGetterThrowsException() {
        MatchStats stats = new MatchStats(match);
        stats.obtainCurrentRoundTeamScore(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMatchScoreGetterThrowsException() {
        MatchStats stats = new MatchStats(match);
        stats.obtainTotalMatchScore(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetScoreThrowsException() {
        MatchStats stats = new MatchStats(match);
        stats.setScore(2, 50);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetMeldThrowsException() {
        MatchStats stats = new MatchStats(match);
        stats.setMeld(2, FOUR_JACKS);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testCancelThrowsExceptionForInvalidIndex() {
        MatchStats stats = new MatchStats(match);
        stats.cancelLastRound(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCancelThrowsExceptionWhenNothingToCancel() {
        MatchStats stats = new MatchStats(match);
        stats.cancelLastRound(0);
    }

    @Test
    public void testAllTeamsHaveReachedGoalReturnsTrue() {
        MatchStats stats = new MatchStats(match);
        setTeamScores(stats, 1020, 1050);
        assertTrue(stats.allTeamsHaveReachedGoal());
    }

    @Test
    public void testAllTeamsHaveReachedGoalReturnsFalse() {
        MatchStats stats = new MatchStats(match);
        setTeamScores(stats, 1020, 500);
        assertFalse(stats.allTeamsHaveReachedGoal());
    }

    private void setTeamScores(MatchStats stats, int firstTeamScore, int secondTeamScore) {
        stats.setScore(0, firstTeamScore);
        stats.setScore(1, secondTeamScore);
        stats.finishRound();
    }

}
