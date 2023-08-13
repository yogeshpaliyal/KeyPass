package com.yogeshpaliyal.keypass

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.KeyGenerator
import javax.crypto.KeyGeneratorSpi
import javax.crypto.SecretKey

internal class FakeKeyGenerator : KeyGeneratorSpi() {
    private val wrapped = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES)
    private var keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private var spec: KeyGenParameterSpec? = null

    override fun engineInit(random: SecureRandom) {
        throw UnsupportedOperationException(
            "Cannot initialize without a ${KeyGenParameterSpec::class.java.name} parameter"
        )
    }

    override fun engineInit(params: AlgorithmParameterSpec?, random: SecureRandom) {
        if (params == null || params !is KeyGenParameterSpec) {
            throw InvalidAlgorithmParameterException(
                "Cannot initialize without a ${KeyGenParameterSpec::class.java.name} parameter"
            )
        }
        spec = params
    }

    override fun engineInit(keysize: Int, random: SecureRandom?) {
        throw UnsupportedOperationException(
            "Cannot initialize without a ${KeyGenParameterSpec::class.java.name} parameter"
        )
    }

    override fun engineGenerateKey(): SecretKey {
        val spec = spec ?: throw IllegalStateException("Not initialized")

        val secretKey = wrapped.generateKey()
        keyStore.setKeyEntry(
            spec.keystoreAlias,
            secretKey,
            null,
            null
        )
        return secretKey
    }
}