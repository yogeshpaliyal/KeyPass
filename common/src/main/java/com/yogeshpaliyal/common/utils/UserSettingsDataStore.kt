package com.yogeshpaliyal.common.utils

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.yogeshpaliyal.common.data.UserSettings

class UserSettingsDataStore(val context: Context) {

    private fun getSerializer(): Serializer<UserSettings> {
        return UserSettingsSerializer(CryptoManager())
    }

    private val Context.dataStoreSerializer by dataStore(
        fileName = "user-settings.json",
        serializer = getSerializer()
    )

    fun getDataStore() = context.dataStoreSerializer
}
