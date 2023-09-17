package com.yogeshpaliyal.common.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray, outputStream: OutputStream) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val blockSize = cipher.blockSize
        val iv = cipher.iv

        outputStream.use {
            it.write(iv.size)
            it.write(iv)

            var offset = 0
            while (offset < bytes.size) {
                val blockSizeToWrite = Math.min(blockSize, bytes.size - offset)
                val encryptedBytes = cipher.update(bytes, offset, blockSizeToWrite)
                if (encryptedBytes != null) {
                    it.write(encryptedBytes)
                }
                offset += blockSizeToWrite
            }

            val finalEncryptedBytes = cipher.doFinal()
            it.write(finalEncryptedBytes)
        }
    }

    fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {

            val ivSize = inputStream.read()
            val iv = ByteArray(ivSize)
            inputStream.read(iv)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))

            val blockSize = cipher.blockSize
            val buffer = ByteArray(blockSize)
            val byteArrayOutputStream = ByteArrayOutputStream()

            var bytesRead = inputStream.read(buffer)
            while (bytesRead != -1) {
                val decryptedBytes = cipher.update(buffer, 0, bytesRead)
                if (decryptedBytes != null) {
                    byteArrayOutputStream.write(decryptedBytes)
                }
                bytesRead = inputStream.read(buffer)
            }

            val finalDecryptedBytes = cipher.doFinal()
            byteArrayOutputStream.write(finalDecryptedBytes)
            return byteArrayOutputStream.toByteArray()
        }
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}
