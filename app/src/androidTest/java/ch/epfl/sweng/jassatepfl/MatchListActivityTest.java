package ch.epfl.sweng.jassatepfl;


import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public final class MatchListActivityTest extends
        ActivityInstrumentationTestCase2<MatchListActivity> {

    public MatchListActivityTest() {
        super(MatchListActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getActivity();
    }

    /*@Test
    public void testSwitchToMapFromList() {
        onView(withId(R.id.list_menu_button)).perform(click());
        onView(withId(R.id.maps_menu_button)).check(matches(isDisplayed()));
    }*/

}
