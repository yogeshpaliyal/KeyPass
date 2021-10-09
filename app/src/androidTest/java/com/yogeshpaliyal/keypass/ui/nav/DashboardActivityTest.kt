package com.yogeshpaliyal.keypass.ui.nav

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.data.AccountModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
class DashboardActivityTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = ActivityScenarioRule(DashboardActivity::class.java)
    

    @Inject
    lateinit var appDatabase: AppDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
        appDatabase.clearAllTables()
    }

    fun getDummyAccount() : AccountModel{
        val accountModel = AccountModel()
        accountModel.title = "Github ${System.currentTimeMillis()}"
        accountModel.username = "yogeshpaliyal"
        accountModel.password = "1234567890"
        accountModel.tags = "social"
        accountModel.site = "https://yogeshpaliyal.com"
        accountModel.notes = "Testing Notes"
        return accountModel
    }

    @Test
    fun addAccountTest(){
        // Navigate to add screen
        onView(withId(R.id.btnAdd)).perform(click())

        val accountModel = getDummyAccount()

        // Fill information on Detail Activity
        onView(withId(R.id.etAccountName)).perform(replaceText(accountModel.title))

        // generate random password
        onView(withId(R.id.etUsername)).perform(replaceText(accountModel.username))

        onView(withId(R.id.etPassword)).perform(replaceText(accountModel.password))

        onView(withId(R.id.etTag)).perform(replaceText(accountModel.tags))

        onView(withId(R.id.etWebsite)).perform(replaceText(accountModel.password))

        onView(withId(R.id.etNotes)).perform(replaceText(accountModel.notes))

        onView(withId(R.id.btnSave)).perform(click())

        onView(withText(accountModel.username)).check(matches(isDisplayed()))
    }




}