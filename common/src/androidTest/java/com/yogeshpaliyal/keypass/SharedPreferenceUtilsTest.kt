package com.yogeshpaliyal.keypass

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yogeshpaliyal.common.data.DEFAULT_PASSWORD_LENGTH
import com.yogeshpaliyal.common.data.UserSettings
import com.yogeshpaliyal.common.utils.clearBackupKey
import com.yogeshpaliyal.common.utils.dataStore
import com.yogeshpaliyal.common.utils.getUserSettings
import com.yogeshpaliyal.common.utils.setDefaultPasswordLength
import com.yogeshpaliyal.common.utils.setKeyPassPassword
import com.yogeshpaliyal.common.utils.setUserSettings
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferenceUtilsTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        FakeAndroidKeyStoreProvider.setup()
        runBlocking {
            context.setUserSettings(UserSettings())
        }
    }

    @Test
    fun getKeyPassPasswordLength_test() = runBlocking {
        val result = context.getUserSettings().defaultPasswordLength
        assertEquals(DEFAULT_PASSWORD_LENGTH, result)
    }

    @Test
    fun setKeyPassPasswordLength_test() = runBlocking {
        val expectedLength = 8f
        context.setDefaultPasswordLength(expectedLength)
        val result = context.getUserSettings().defaultPasswordLength
        assertEquals(expectedLength, result)
    }


    @Test
    fun getKeyPassPassword() = runBlocking {
        val result = context.getUserSettings().keyPassPassword
        assertEquals(null, result)
    }

    @Test
    fun setKeyPassPassword() = runBlocking {
        val password = "hello"
        context.setKeyPassPassword(password)
//        val readAsync = async { context.getUserSettings().keyPassPassword }
//        val writeAsync = async {  context.setKeyPassPassword(password) }
//
//        readAsync.await()
//        writeAsync.await()

        val result = context.getUserSettings().keyPassPassword
        assertEquals(password, result)
    }

    @After
    fun clear() {
        runBlocking {
            context.setUserSettings(UserSettings())
        }
    }
}
