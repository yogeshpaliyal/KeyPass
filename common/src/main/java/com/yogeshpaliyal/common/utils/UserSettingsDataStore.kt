package com.yogeshpaliyal.common.utils

import android.content.Context
import android.os.Build
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.yogeshpaliyal.common.data.UserSettings

class UserSettingsDataStore(val context: Context) {

    private fun getSerializer(context: Context): Serializer<UserSettings> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            UserSettingsSerializer(CryptoManager())
        } else {
            UserSettingsSerializerLegacy(context)
        }
    }

    private val Context.dataStoreSerializer by dataStore(
        fileName = "user-settings.json",
        serializer = getSerializer(context)
    )

    fun getDataStore() = context.dataStoreSerializer
}
