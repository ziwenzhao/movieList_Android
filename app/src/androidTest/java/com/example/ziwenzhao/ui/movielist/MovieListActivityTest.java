package com.example.ziwenzhao.ui.movielist;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.ziwenzhao.Utils.EspressoIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieListActivityTest {

    @Rule
    public ActivityTestRule<MovieListActivity> activityRule =
            new ActivityTestRule<>(MovieListActivity.class);

    @Before
    public void setup() {
        // Register IdlingResource in order to properly test UI along with background tasks
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void finish() {
        // unregister IdlingResource to conserve system resources
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void shouldDisplayTextView() {

        onView(withText("Alita: Battle Angel")).check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayImageView() {

        onView(withText("Alita: Battle Angel")).check(matches(hasSibling(withId(R.id.image))));

    }

    @Test
    public void shouldHasMutipleItems() {
        onView(withId(R.id.recycler_view)).check(matches(hasMinimumChildCount(5)));
    }

    @Test
    public void shouldRefresh() {
        onView(withId(R.id.recycler_view)).perform(swipeUp());
        onView(withText("Alita: Battle Angel")).check(doesNotExist());
    }
}