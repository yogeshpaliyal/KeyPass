package com.yogeshpaliyal.common.dbhelper

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.security.crypto.MasterKey
import java.security.Key
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator


object EncryptionHelper2 {
    @RequiresApi(Build.VERSION_CODES.M)
    fun encrypt(applicationContext: Context) {
        val alias = "keyPassSalt"

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        // Generate a new key if it doesn't exist
        if (!keyStore.containsAlias(alias)) {
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val builder = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )


            builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            builder.setRandomizedEncryptionRequired(false) // If you don't need randomization, set this to false
            builder.setKeySize(256) // Set the desired key size
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        }

        val secretKey: Key = keyStore.getKey(alias, null)
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encryptedData = cipher.doFinal("data".toByteArray())


    }
}