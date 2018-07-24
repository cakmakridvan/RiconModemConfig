package test;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.database.KitapEkle;
import com.database.MainActivity;
import com.database.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class KayitEkleTesting {

    @Rule
    public ActivityTestRule<KitapEkle> mkayit_ekle = new ActivityTestRule<>(KitapEkle.class);

    @Test
    public void kayitekle() {

        onView(withId(R.id.editText1)).perform(typeText("Sok Market Maslak"));
        onView(withId(R.id.editText2)).perform(typeText("35"));
        onView(withId(R.id.editText3)).perform(typeText("535654347"), closeSoftKeyboard());


        onView(withId(R.id.button1)).perform(click());
    }

}
