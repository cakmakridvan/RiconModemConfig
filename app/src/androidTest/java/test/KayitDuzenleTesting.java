package test;

import android.support.test.espresso.Root;
import android.support.test.rule.ActivityTestRule;

import com.database.KitapDuzenle;
import com.database.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import routerconfigration.DashBoard;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;


public class KayitDuzenleTesting {

    @Rule
    public ActivityTestRule<DashBoard> mdashboard = new ActivityTestRule<>(DashBoard.class);

    @Test
    public void dashboard() {


        onView(withId(R.id.btn_rehber)).perform(click());
        onView(withId(R.id.btn_duzenle)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.list_view)).atPosition(0).perform(click());

/*
        onView(withId(R.id.edt_search)).perform(typeText("ridvan"));
        onView(withId(R.id.list_view)).perform(click());
*/



        onView(withId(R.id.button1)).perform(click());


        onView(withId(R.id.editText3)).perform(clearText(),typeText("5556678894"), closeSoftKeyboard());


        onView(withId(R.id.button1)).perform(click());

        onView(withText(R.string.toast_msg)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));


    }
}
