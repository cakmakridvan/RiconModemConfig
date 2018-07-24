package test;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.database.KitapEkle;
import com.database.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import routerconfigration.DashBoard;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DashBoardTesting {

    @Rule
    public ActivityTestRule<DashBoard> mdashboard = new ActivityTestRule<>(DashBoard.class);

    @Test
    public void dashboard() {


        onView(withId(R.id.btn_rehber)).perform(click());
        onView(withId(R.id.btn_yenikayit)).perform(click());

        onView(withId(R.id.editText1)).perform(typeText("Sok Market Adana"));
        onView(withId(R.id.editText2)).perform(typeText("36"));
        onView(withId(R.id.editText3)).perform(typeText("535654347"), closeSoftKeyboard());


        onView(withId(R.id.button1)).perform(click());

    }
}
