package com.yogeshpaliyal.keypass.ui.nav

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DashboardActivityTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = createAndroidComposeRule<DashboardActivity>()

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
        activityScenarioRule.onNodeWithTag("accountName").performTextInput(accountModel.title ?: "")

        // generate random password
        activityScenarioRule.onNodeWithTag("username").performTextInput(accountModel.username ?: "")

        activityScenarioRule.onNodeWithTag("password").performTextInput(accountModel.password ?: "")

        activityScenarioRule.onNodeWithTag("tags").performTextInput(accountModel.tags ?: "")

        activityScenarioRule.onNodeWithTag("website").performTextInput(accountModel.site ?: "")

        activityScenarioRule.onNodeWithTag("notes").performTextInput(accountModel.notes ?: "")

        activityScenarioRule.onNodeWithTag("save").performClick()

        // is showing in listing
        activityScenarioRule.onNodeWithText(accountModel.username?:"").assertIsDisplayed()
    }

    private fun checkAccountDetail(accountModel: AccountModel) {
        // Navigate to account detail
        activityScenarioRule.onNodeWithText(accountModel.username?:"").performClick()

        // Fill information on Detail Activity
        activityScenarioRule.onNodeWithTag("accountName").assertTextEquals(accountModel.title ?: "")

        // generate random password
        activityScenarioRule.onNodeWithTag("username").assertTextEquals(accountModel.username ?: "")

        activityScenarioRule.onNodeWithTag("password").assertTextEquals(accountModel.password ?: "")

        activityScenarioRule.onNodeWithTag("tags").assertTextEquals(accountModel.tags ?: "")

        activityScenarioRule.onNodeWithTag("website").assertTextEquals(accountModel.site ?: "")

        activityScenarioRule.onNodeWithTag("notes").assertTextEquals(accountModel.notes ?: "")
    }

    private fun deleteAccount(accountModel: AccountModel) {
        // delete account
        activityScenarioRule.onNodeWithTag("action_delete").performClick()

        activityScenarioRule.onNodeWithTag("delete").performClick()

        // is not showing in listing
        activityScenarioRule.onNodeWithText(accountModel.username ?: "").assertDoesNotExist()
    }
}
