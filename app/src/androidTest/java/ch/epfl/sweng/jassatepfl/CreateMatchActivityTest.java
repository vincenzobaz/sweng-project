package ch.epfl.sweng.jassatepfl;

import android.support.test.espresso.contrib.PickerActions;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ch.epfl.sweng.jassatepfl.injections.InjectedBaseActivityTest;
import ch.epfl.sweng.jassatepfl.model.Match;
import ch.epfl.sweng.jassatepfl.test_utils.ToastMatcher;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.jassatepfl.model.Match.GameVariant.CLASSIC;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Test class for CreateMatchActivity
 *
 * @author Alexis Montavon
 */
public final class CreateMatchActivityTest extends InjectedBaseActivityTest {

    DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.FRENCH);

    public CreateMatchActivityTest() {
        super(CreateMatchActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @Test
    public void testSwitchToInvitePlayerActivity() {
        getActivity();
        onView(withId(R.id.add_player_button)).perform(click());
        onView(withId(R.id.invite_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testSpinnerSelection() {
        onView(withId(R.id.variant_spinner)).perform(click());
        onData(allOf(is(instanceOf(Match.GameVariant.class)), is(CLASSIC))).perform(click());
    }

    @Test
    public void testInputDescription() {
        onView(withId(R.id.description_match_text)).check(matches(withHint(R.string.description)));
        onView(withId(R.id.description_match_text)).perform(typeText("Hello World"));
        onView(withId(R.id.description_match_text)).check(matches(withText("Hello World")));
    }

    @Test
    public void testPrivacySwitch() {
        onView(withId(R.id.switch_private)).perform(click());
        onView(withId(R.id.switch_private)).check(matches(isChecked()));
    }

    @Test
    public void testTimePickerSetsTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(HOUR_OF_DAY, 1);
        calendar.add(MINUTE, 5);
        setTime(calendar.get(HOUR_OF_DAY), calendar.get(MINUTE));
        onView(withId(R.id.current_expiration_time))
                .check(matches(withText(dateFormat.format(calendar.getTimeInMillis()))));
    }

    @Test
    public void testTimePickerDisplaysToastForInvalidTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(HOUR_OF_DAY);
        calendar.add(MINUTE, -5);
        setTime(currentHour == 23 ? 0 : currentHour, calendar.get(MINUTE));
        onView(withText(R.string.create_toast_invalid_hour)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDatePickerSetsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(HOUR_OF_DAY, 1);
        calendar.add(DAY_OF_MONTH, 3);
        calendar.add(MONTH, 1);
        setDate(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH));
        onView(withId(R.id.current_expiration_time))
                .check(matches(withText(dateFormat.format(calendar.getTimeInMillis()))));
    }

    @Test
    public void testDatePickerDisplaysToastForInvalidDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(DAY_OF_MONTH, -5);
        setDate(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH));
        onView(withText(R.string.create_toast_invalid_date)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDatePickerSetsHourWhenConflictWithCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(YEAR);
        int currentMonth = calendar.get(MONTH);
        int currentDay = calendar.get(DAY_OF_MONTH);
        calendar.add(DAY_OF_MONTH, 6);
        setDate(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH));
        calendar.add(MINUTE, -5);
        setTime(calendar.get(HOUR_OF_DAY), calendar.get(MINUTE));
        setDate(currentYear, currentMonth, currentDay);
        calendar = Calendar.getInstance();
        onView(withId(R.id.current_expiration_time))
                .check(matches(withText(dateFormat.format(calendar.getTimeInMillis()))));
    }

    /* Not working because we need to use intentsTestRule, but it will probably not work with Jenkins
    @Test
    public void testPlacePickerResultIsHandled() {
        Intent resultData = new Intent();
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage("com.google.android.gms")).respondWith(result);
        onView(withId(R.id.create_place_picker_button)).perform(click());
    }
    */

    private void setTime(int hour, int minute) {
        onView(withId(R.id.time_picker_button)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(hour, minute));
        onView(withId(android.R.id.button1)).perform(click());
    }

    private void setDate(int year, int month, int dayOfMonth) {
        onView(withId(R.id.date_picker_button)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year, month + 1, dayOfMonth));
        onView(withId(android.R.id.button1)).perform(click());
    }

}
