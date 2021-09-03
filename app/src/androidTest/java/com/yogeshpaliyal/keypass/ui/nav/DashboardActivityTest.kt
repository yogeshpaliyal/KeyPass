package com.yogeshpaliyal.keypass.ui.nav


import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewInteraction
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*

import com.yogeshpaliyal.keypass.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`

@LargeTest
@RunWith(AndroidJUnit4::class)
class DashboardActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(DashboardActivity::class.java)

    @Test
    fun mainActivityTest() {
        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnAdd),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        1
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.etAccountName),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilAccountName),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("test acco"), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.etUsername),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilUsername),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText("yogeshpaliyal123"), closeSoftKeyboard())

        val checkableImageButton = onView(
            allOf(
                withId(R.id.text_input_start_icon),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        checkableImageButton.perform(click())

        val editText = onView(
            allOf(
                withId(R.id.etPassword),
                withParent(withParent(withId(R.id.tilPassword))),
                isDisplayed()
            )
        )
        editText.check(matches(withText("W47@OHW5KZ")))

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.etTag),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilTags),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText3.perform(replaceText("test tag"), closeSoftKeyboard())

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.etWebsite),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilWebsite),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText4.perform(replaceText("https://yoge"), closeSoftKeyboard())

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.etNotes),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilNotes),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText5.perform(replaceText("notrs1"), closeSoftKeyboard())

        val textInputEditText6 = onView(
            allOf(
                withId(R.id.etNotes), withText("notrs1"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilNotes),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText6.perform(click())

        val textInputEditText7 = onView(
            allOf(
                withId(R.id.etNotes), withText("notrs1"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilNotes),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText7.perform(click())

        val textInputEditText8 = onView(
            allOf(
                withId(R.id.etNotes), withText("notrs1"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilNotes),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText8.perform(replaceText("notes1"))

        val textInputEditText9 = onView(
            allOf(
                withId(R.id.etNotes), withText("notes1"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilNotes),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText9.perform(closeSoftKeyboard())

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.btnSave),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton2.perform(click())

        val appCompatImageButton = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.bottomAppBar),
                        childAtPosition(
                            withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val view = onView(
            allOf(
                withId(R.id.scrim_view),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_nav_drawer),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        view.perform(click())

        val view2 = onView(
            allOf(
                withId(R.id.scrim_view),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_nav_drawer),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        view2.perform(click())

        val maskedCardView = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recycler_view),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        maskedCardView.perform(click())

        val editText2 = onView(
            allOf(
                withId(R.id.etAccountName), withText("test acco"),
                withParent(withParent(withId(R.id.tilAccountName))),
                isDisplayed()
            )
        )
        editText2.check(matches(withText("test acco")))

        val editText3 = onView(
            allOf(
                withId(R.id.etUsername), withText("yogeshpaliyal123"),
                withParent(withParent(withId(R.id.tilUsername))),
                isDisplayed()
            )
        )
        editText3.check(matches(withText("yogeshpaliyal123")))

        val editText4 = onView(
            allOf(
                withId(R.id.etPassword), withText("W47@OHW5KZ"),
                withParent(withParent(withId(R.id.tilPassword))),
                isDisplayed()
            )
        )
        editText4.check(matches(withText("W47@OHW5KZ")))

        val editText5 = onView(
            allOf(
                withId(R.id.etTag), withText("test tag"),
                withParent(withParent(withId(R.id.tilTags))),
                isDisplayed()
            )
        )
        editText5.check(matches(withText("test tag")))

        val editText6 = onView(
            allOf(
                withId(R.id.etWebsite), withText("https://yoge"),
                withParent(withParent(withId(R.id.tilWebsite))),
                isDisplayed()
            )
        )
        editText6.check(matches(withText("https://yoge")))

        val editText7 = onView(
            allOf(
                withId(R.id.etNotes), withText("notes1"),
                withParent(withParent(withId(R.id.tilNotes))),
                isDisplayed()
            )
        )
        editText7.check(matches(withText("notes1")))

        val cardView = onView(
            allOf(
                withParent(
                    allOf(
                        withId(R.id.nestedScrollView),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        cardView.check(matches(isDisplayed()))

        val floatingActionButton3 = onView(
            allOf(
                withId(R.id.btnSave),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton3.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
