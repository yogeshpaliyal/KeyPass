package com.yogeshpaliyal.keypass.ui.nav

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DashboardActivityTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = ActivityScenarioRule(DashboardActivity::class.java)

    @Inject
    lateinit var appDatabase: com.yogeshpaliyal.common.AppDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
        appDatabase.clearAllTables()
    }

    private fun getDummyAccount(): AccountModel {
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
    fun addAccountAndDetailAndDeleteTest() {
        val accountModel = getDummyAccount()
        addAccount(accountModel)
        checkAccountDetail(accountModel)
        deleteAccount(accountModel)
    }

    private fun addAccount(accountModel: AccountModel) {
        // Navigate to add screen
        onView(withId(R.id.btnAdd)).perform(click())

        // Fill information on Detail Activity
        onView(withId(R.id.etAccountName)).perform(replaceText(accountModel.title))

        // generate random password
        onView(withId(R.id.etUsername)).perform(replaceText(accountModel.username))

        onView(withId(R.id.etPassword)).perform(replaceText(accountModel.password))

        onView(withId(R.id.etTag)).perform(replaceText(accountModel.tags))

        onView(withId(R.id.etWebsite)).perform(replaceText(accountModel.password))

        onView(withId(R.id.etNotes)).perform(replaceText(accountModel.notes))

        onView(withId(R.id.btnSave)).perform(click())

        // is showing in listing
        onView(withText(accountModel.username)).check(matches(isDisplayed()))
    }

    private fun checkAccountDetail(accountModel: AccountModel) {
        // Navigate to account detail
        onView(withText(accountModel.username)).perform(click())

        // Fill information on Detail Activity
        onView(withId(R.id.etAccountName)).check(matches(withText(accountModel.title)))

        // generate random password
        onView(withId(R.id.etUsername)).check(matches(withText(accountModel.username)))

        onView(withId(R.id.etPassword)).check(matches(withText(accountModel.password)))

        onView(withId(R.id.etTag)).check(matches(withText(accountModel.tags)))

        onView(withId(R.id.etWebsite)).check(matches(withText(accountModel.password)))

        onView(withId(R.id.etNotes)).check(matches(withText(accountModel.notes)))
    }

    private fun deleteAccount(accountModel: AccountModel) {
        // delete account
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(R.string.delete)).perform(click())

        // is not showing in listing
        onView(withText(accountModel.username)).check(doesNotExist())
    }
}
