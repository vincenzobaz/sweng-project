package ch.epfl.sweng.jassatepfl;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewParent;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.sweng.jassatepfl.model.Match;
import ch.epfl.sweng.jassatepfl.model.Match.Meld;
import ch.epfl.sweng.jassatepfl.stats.MatchStats;
import ch.epfl.sweng.jassatepfl.test_utils.DummyDataTest;
import ch.epfl.sweng.jassatepfl.test_utils.ToastMatcherTest;

import static android.app.Activity.RESULT_CANCELED;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.init;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.release;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.jassatepfl.model.Match.Meld.FIFTY;
import static ch.epfl.sweng.jassatepfl.model.Match.Meld.FOUR_JACKS;
import static ch.epfl.sweng.jassatepfl.model.Match.Meld.FOUR_NINE;
import static ch.epfl.sweng.jassatepfl.model.Match.Meld.HUNDRED;
import static ch.epfl.sweng.jassatepfl.model.Match.Meld.MARRIAGE;
import static ch.epfl.sweng.jassatepfl.model.Match.Meld.THREE_CARDS;
import static ch.epfl.sweng.jassatepfl.test_utils.DummyDataTest.bricoloBob;
import static ch.epfl.sweng.jassatepfl.test_utils.DummyDataTest.fullMatchWithBob;
import static ch.epfl.sweng.jassatepfl.test_utils.DummyDataTest.jimmy;
import static ch.epfl.sweng.jassatepfl.test_utils.DummyDataTest.nicolas;
import static ch.epfl.sweng.jassatepfl.test_utils.DummyDataTest.vincenzo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public final class GameActivityTest extends InjectedBaseActivityTest {

    private final Match ownedMatch = DummyDataTest.ownedMatch();

    @Rule
    public ActivityTestRule<GameActivity> activityRule =
            new ActivityTestRule<>(GameActivity.class, false, false);

    @Test
    public void testElementsAreDisplayedForOwner() {
        ownedMatchSetUp();
        onView(withId(R.id.score_update_cancel)).check(matches(isDisplayed()));
        int pointsGoal = ownedMatch.getGameVariant().getPointGoal();
        String playingTo = String.format(getInstrumentation().getTargetContext().getResources()
                .getString(R.string.game_text_point_goal), Integer.toString(pointsGoal));
        onView(withId(R.id.game_playing_to)).check(matches(withText(playingTo)));
    }

    @Test
    public void testElementsAreHiddenForRegularPlayer() {
        dbRefWrapTest.reset();
        Intent intent = new Intent();
        intent.putExtra("match_Id", fullMatchWithBob().getMatchID());
        intent.putExtra("mode", "online");

        Set<Match> matches = new HashSet<>();
        matches.add(fullMatchWithBob());
        dbRefWrapTest.addMatches(matches);
        Set<MatchStats> stats = new HashSet<>();
        stats.add(new MatchStats(fullMatchWithBob()));
        dbRefWrapTest.addStats(stats);
        dbRefWrapTest.addPlayers(DummyDataTest.players());

        activityRule.launchActivity(intent);
        onView(withId(R.id.score_update_cancel)).check(matches(not(isDisplayed())));
        onView(withId(R.id.score_update_1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.score_meld_spinner_2)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testCancelDisplaysToastWhenNoCancelAvailable() {
        ownedMatchSetUp();
        onView(withId(R.id.score_update_cancel)).perform(click());
        onView(withText(R.string.toast_cannot_cancel)).inRoot(new ToastMatcherTest())
                .check(matches(isDisplayed()));
        onView(withId(R.id.score_update_cancel)).check(matches(not(isEnabled())));
    }

    @Test
    public void testPlayersNamesAreDisplayed() {
        ownedMatchSetUp();
        onView(withId(R.id.team_members_1))
                .check(matches(withText(bricoloBob.getFirstName() + ", " + jimmy.getFirstName())));
        onView(withId(R.id.team_members_2))
                .check(matches(withText(nicolas.getFirstName() + ", " + vincenzo.getFirstName())));
    }

    @Test
    public void testPlayersNamesAreNotDisplayedInOfflineMode() {
        offlineMatchSetup();
        onView(withId(R.id.team_members_1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.team_members_2)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testUpdateScore() {
        ownedMatchSetUp();
        incrementScore(0, "2", false);
        checkScoreDisplay("2", "155");
        incrementScore(1, "5", false);
        checkScoreDisplay("154", "160");
    }

    @Test
    public void testMatchButton() {
        ownedMatchSetUp();
        onView(withId(R.id.score_update_1)).perform(click());
        onView(withId(R.id.score_picker_match)).perform(click());
        checkScoreDisplay("257", "0");
        onView(withId(R.id.score_update_2)).perform(click());
        onView(withId(R.id.score_picker_match)).perform(click());
        checkScoreDisplay("257", "257");
    }

    @Test
    public void testCancelUpdateDoesNotUpdateScore() {
        ownedMatchSetUp();
        incrementScore(0, "50", false);
        checkScoreDisplay("50", "107");
        onView(withId(R.id.score_update_1)).perform(click());
        onView(withId(R.id.score_picker_cancel)).perform(click());
        checkScoreDisplay("50", "107");
        onView(withId(R.id.score_update_2)).perform(click());
        onView(withId(R.id.score_picker_cancel)).perform(click());
        checkScoreDisplay("50", "107");
    }

    @Test
    public void testCancelLastRoundResetsScore() {
        ownedMatchSetUp();
        incrementScore(0, "60", false);
        checkScoreDisplay("60", "97");
        incrementScore(1, "33", false);
        checkScoreDisplay("184", "130");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("60", "97");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("0", "0");
    }

    @Test
    public void testDisplayEndOfMatchMessage() {
        offlineMatchSetup();
        for (int i = 0; i < 4; ++i) {
            onView(withId(R.id.score_update_1)).perform(click());
            onView(withId(R.id.score_picker_match)).perform(click());
        }
        String message = String.format(getInstrumentation().getTargetContext()
                .getResources().getString(R.string.dialog_game_end), "Team 1");
        onView(withText(message)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddingMeldUpdatesScore() {
        ownedMatchSetUp();
        addMeld(0, MARRIAGE);
        checkScoreDisplay("20", "0");
        addMeld(1, FOUR_JACKS);
        checkScoreDisplay("20", "200");
    }

    @Test
    public void testCancelLastMeld() {
        ownedMatchSetUp();
        incrementScore(1, "100", false);
        checkScoreDisplay("57", "100");
        addMeld(0, FOUR_JACKS);
        checkScoreDisplay("257", "100");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("57", "100");
    }

    @Test
    public void testCancelSequenceIsCorrect() {
        ownedMatchSetUp();
        addMeld(0, FOUR_NINE);
        incrementScore(0, "100", false);
        addMeld(1, FIFTY);
        incrementScore(1, "50", false);
        addMeld(0, HUNDRED);
        addMeld(0, THREE_CARDS);
        checkScoreDisplay("477", "157");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("457", "157");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("357", "157");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("250", "107");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("250", "57");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("150", "0");
        onView(withId(R.id.score_update_cancel)).perform(click());
        checkScoreDisplay("0", "0");
    }

    @Test
    public void testCorrectWinnerIsDisplayedWhenBothTeamsHaveReachedGoal() {
        offlineMatchSetup();
        for (int i = 0; i < 3; ++i) {
            onView(withId(R.id.score_update_1)).perform(click());
            onView(withId(R.id.score_picker_match)).perform(click());
            onView(withId(R.id.score_update_2)).perform(click());
            onView(withId(R.id.score_picker_match)).perform(click());
        }
        addMeld(0, FOUR_JACKS);
        addMeld(1, FOUR_JACKS);
        incrementScore(1, "50", false);
        String message = String.format(getInstrumentation().getTargetContext()
                .getResources().getString(R.string.dialog_game_end), "Team 2");
        onView(withText(message)).check(matches(isDisplayed()));
    }

    @Test
    public void testReturnToMainActivityIntentIsSentWhenMatchIsOver() {
        init();
        offlineMatchSetup();

        for (int i = 0; i < 4; ++i) {
            onView(withId(R.id.score_update_1)).perform(click());
            onView(withId(R.id.score_picker_match)).perform(click());
        }

        Matcher<Intent> expectedIntent = hasComponent(MainActivity.class.getName());
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(RESULT_CANCELED, null));
        onView(withId(android.R.id.button1)).perform(click());
        intended(expectedIntent);
        release();
    }

    @Test
    public void testHistoryDisplay() {
        ownedMatchSetUp();
        onView(withId(R.id.score_display_1)).perform(click());
        onView(withId(R.id.score_table_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.score_table_layout)).perform(pressBack());
    }

    @Test
    public void testHistoryIsCorrect() {
        ownedMatchSetUp();
        incrementScore(1, "50", false);
        incrementScore(0, "7", false);
        addMeld(1, THREE_CARDS);
        onView(withId(R.id.score_display_2)).perform(click());
        onView(atPositionInTable(1, 1)).check(matches(withText("50")));
        onView(atPositionInTable(1, 2)).check(matches(withText("150")));
        onView(atPositionInTable(2, 3)).check(matches(withText(THREE_CARDS.toString())));
        onView(withId(R.id.score_table_layout)).perform(pressBack());
    }

    @Test
    public void testSetGoalIsDisabledInOnlineMode() {
        ownedMatchSetUp();
        onView(withId(R.id.game_playing_to)).check(matches(not(isClickable())));
    }

    @Test
    public void testSetGoalSetsGoal() {
        offlineMatchSetup();
        onView(withId(R.id.game_playing_to)).check(matches(isClickable()));
        incrementScore(-1, "700", false);
        String playingTo = String.format(getInstrumentation().getTargetContext().getResources()
                .getString(R.string.game_text_point_goal), Integer.toString(700));
        onView(withId(R.id.game_playing_to)).check(matches(withText(playingTo)));
    }

    @Test
    public void testCancelSetGoalDoesNotUpdateGoal() {
        offlineMatchSetup();
        String playingTo = String.format(getInstrumentation().getTargetContext().getResources()
                .getString(R.string.game_text_point_goal), Integer.toString(1000));
        onView(withId(R.id.game_playing_to)).check(matches(withText(playingTo)));
        onView(withId(R.id.game_playing_to)).perform(click());
        onView(withId(R.id.score_picker_cancel)).perform(click());
        onView(withId(R.id.game_playing_to)).check(matches(withText(playingTo)));
    }

    @Test
    public void testDoublePoints() {
        ownedMatchSetUp();
        incrementScore(0, "100", true);
        checkScoreDisplay("200", "114");
        incrementScore(1, "50", true);
        checkScoreDisplay("414", "214");
    }

    @Test
    public void testNumpadCorrect() {
        ownedMatchSetUp();
        onView(withId(R.id.score_update_1)).perform(click());
        onView(withId(R.id.numpad_1)).perform(click());
        onView(withId(R.id.numpad_3)).perform(click());
        onView(withId(R.id.numpad_4)).perform(click());
        onView(withId(R.id.numpad_correct)).perform(click());
        onView(withId(R.id.numpad_9)).perform(click());
        onView(withId(R.id.score_picker_confirm)).perform(click());
        checkScoreDisplay("139", "18");
    }

    @Test
    public void testNumpadCorrectReturns() {
        ownedMatchSetUp();
        onView(withId(R.id.score_update_2)).perform(click());
        onView(withId(R.id.numpad_0)).perform(click());
        onView(withId(R.id.numpad_2)).perform(click());
        onView(withId(R.id.numpad_6)).perform(click());
        onView(withId(R.id.numpad_correct)).perform(click());
        onView(withId(R.id.numpad_correct)).perform(click());
        onView(withId(R.id.numpad_correct)).perform(click());
        onView(withId(R.id.score_picker_confirm)).perform(click());
        checkScoreDisplay("157", "0");
    }

    @Test
    public void testToastIsDisplayedForInvalidPoints() {
        ownedMatchSetUp();
        incrementScore(0, "500", false);
        String pointToast = getInstrumentation().getTargetContext().getResources()
                .getString(R.string.toast_invalid_score);
        pointToast = String.format(pointToast, 157);
        onView(withText(pointToast)).inRoot(new ToastMatcherTest())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSplitGoalsAreHiddenInOnlineMode() {
        ownedMatchSetUp();
        onView(withId(R.id.split_team_goals)).check(matches(not(isDisplayed())));
        onView(withId(R.id.team_goal_1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.team_goal_2)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testSplitGoalsDisplaysBothGoals() {
        offlineMatchSetup();
        onView(withId(R.id.split_team_goals)).perform(click());
        onView(withId(R.id.team_goal_1)).check(matches(isDisplayed()));
        onView(withId(R.id.team_goal_2)).check(matches(isDisplayed()));
        onView(withId(R.id.game_playing_to)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testCanReturnToNormalModeFromSplitMode() {
        offlineMatchSetup();
        onView(withId(R.id.split_team_goals)).perform(click());
        onView(withId(R.id.game_playing_to)).check(matches(not(isDisplayed())));
        onView(withId(R.id.split_team_goals)).perform(click());
        onView(withId(R.id.game_playing_to)).check(matches(isDisplayed()));
    }

    @Test
    public void testSplitGoalsCanUpdateBothTeamsGoals() {
        offlineMatchSetup();
        onView(withId(R.id.split_team_goals)).perform(click());
        incrementScore(2, "456", false);
        onView(withId(R.id.team_goal_1)).check(matches(withText("456")));
        incrementScore(3, "789", false);
        onView(withId(R.id.team_goal_2)).check(matches(withText("789")));
    }

    private void checkScoreDisplay(String firstDisplay, String secondDisplay) {
        onView(withId(R.id.score_display_1)).check(matches(withText(firstDisplay)));
        onView(withId(R.id.score_display_2)).check(matches(withText(secondDisplay)));
    }

    private void incrementScore(int teamIndex, final String value, boolean doubleScore) {
        int button = 0;
        switch (teamIndex) {
            case 0:
                button = R.id.score_update_1;
                break;
            case 1:
                button = R.id.score_update_2;
                break;
            case 2:
                button = R.id.team_goal_1;
                break;
            case 3:
                button = R.id.team_goal_2;
                break;
            case -1:
                button = R.id.game_playing_to;
                break;
        }
        onView(withId(button)).perform(click());
        for (int i = 0; i < value.length(); ++i) {
            int buttonId = buttonId(value.charAt(i));
            onView(withId(buttonId)).perform(click());
        }
        if (doubleScore) {
            onView(withId(R.id.numpad_double_score)).perform(click());
        }
        onView(withId(R.id.score_picker_confirm)).perform(click());
    }

    private int buttonId(char value) {
        switch (value) {
            case '0':
                return R.id.numpad_0;
            case '1':
                return R.id.numpad_1;
            case '2':
                return R.id.numpad_2;
            case '3':
                return R.id.numpad_3;
            case '4':
                return R.id.numpad_4;
            case '5':
                return R.id.numpad_5;
            case '6':
                return R.id.numpad_6;
            case '7':
                return R.id.numpad_7;
            case '8':
                return R.id.numpad_8;
            case '9':
                return R.id.numpad_9;
            default:
                return -1;
        }
    }

    private void addMeld(int teamIndex, Meld meld) {
        int meldSpinner = teamIndex == 0 ? R.id.score_meld_spinner_1 : R.id.score_meld_spinner_2;
        onView(withId(meldSpinner)).perform(click());
        onData(allOf(is(instanceOf(Meld.class)), is(meld))).perform(click());
    }

    private void ownedMatchSetUp() {
        dbRefWrapTest.reset();
        Intent intent = new Intent();
        intent.putExtra("match_Id", ownedMatch.getMatchID());
        intent.putExtra("mode", "online");

        Set<Match> matches = new HashSet<>();
        matches.add(ownedMatch);
        dbRefWrapTest.addMatches(matches);
        Set<MatchStats> stats = new HashSet<>();
        stats.add(new MatchStats(ownedMatch));
        dbRefWrapTest.addStats(stats);
        dbRefWrapTest.addPlayers(DummyDataTest.players());
        activityRule.launchActivity(intent);
    }

    private void offlineMatchSetup() {
        dbRefWrapTest.reset();
        Intent intent = new Intent();
        intent.putExtra("match_Id", ownedMatch.getMatchID());
        intent.putExtra("mode", "offline");
        activityRule.launchActivity(intent);
    }

    private static Matcher<View> atPositionInTable(final int x, final int y) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is at position # " + x + " , " + y);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent viewParent = view.getParent();
                if (!(viewParent instanceof TableRow)) {
                    return false;
                }
                TableRow row = (TableRow) viewParent;
                TableLayout table = (TableLayout) row.getParent();
                return table.indexOfChild(row) == y && row.indexOfChild(view) == x;
            }
        };
    }

}
