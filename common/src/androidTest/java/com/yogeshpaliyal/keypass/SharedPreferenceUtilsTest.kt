package com.yogeshpaliyal.keypass

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yogeshpaliyal.common.utils.dataStore
import com.yogeshpaliyal.common.utils.getKeyPassPasswordLength
import com.yogeshpaliyal.common.utils.setKeyPassPasswordLength
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
    }

    @Test
    fun getKeyPassPasswordLength_test() = runBlocking {
        val result = context.getKeyPassPasswordLength()
        assertEquals(null, result)
    }

    @Test
    fun setKeyPassPasswordLength_test() = runBlocking {
        val expectedLength = 8f
        context.setKeyPassPasswordLength(expectedLength)
        val result = context.getKeyPassPasswordLength()
        assertEquals(expectedLength, result)
    }

    @After
    fun clear() {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }
}
