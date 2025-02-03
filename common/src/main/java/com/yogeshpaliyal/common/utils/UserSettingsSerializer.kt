package com.yogeshpaliyal.common.utils

import androidx.datastore.core.Serializer
import com.yogeshpaliyal.common.data.UserSettings
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class UserSettingsSerializer(
    private val cryptoManager: CryptoManager
) : Serializer<UserSettings> {

    override val defaultValue: UserSettings
        get() = UserSettings()

    override suspend fun readFrom(input: InputStream): UserSettings {
        val decryptedBytes = cryptoManager.decrypt(input)

        val decodedString = decryptedBytes.decodeToString()
        return Json.decodeFromString(
            deserializer = UserSettings.serializer(),
            string = decodedString
        )

    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        val json = Json.encodeToString(
            serializer = UserSettings.serializer(),
            value = t
        )
        cryptoManager.encrypt(
            bytes = json.encodeToByteArray(),
            outputStream = output
        )
    }
}
