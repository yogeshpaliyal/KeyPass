package com.yogeshpaliyal.keypass.ui.nav

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.yogeshpaliyal.keypass.R
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardActivityTest {

    @get:Rule var activityScenarioRule = activityScenarioRule<DashboardActivity>()

    @Before
    fun setUp() {

    }

    @Test
    fun addAccountTest(){
        // Navigate to add screen
        onView(withId(R.id.btnAdd)).perform(click())

        // Fill information on Detail Activity
        onView(withId(R.id.etAccountName)).perform(replaceText("YouTube"))

        // generate random password
        //onView(withId(R.id.text_input_start_icon)).perform(click())
        onView(withId(R.id.etUsername)).perform(replaceText("yogeshpaliyal"))


        onView(withId(R.id.etPassword)).perform(replaceText("1234567890"))

        onView(withId(R.id.etTag)).perform(replaceText("social"))

        onView(withId(R.id.etWebsite)).perform(replaceText("https://yogeshpaliyal.com"))

        onView(withId(R.id.etNotes)).perform(replaceText("Testing Notes"))

        onView(withId(R.id.btnSave)).perform(click())

       // onData(withText("yogeshpaliyal")).check(matches(withText("yogeshpaliyal")))

    }
}