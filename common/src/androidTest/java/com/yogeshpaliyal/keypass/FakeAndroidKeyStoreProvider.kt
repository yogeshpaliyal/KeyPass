package com.yogeshpaliyal.keypass

import java.security.Provider
import java.security.Security

internal class FakeAndroidKeyStoreProvider : Provider(
    "AndroidKeyStore",
    1.0,
    "Fake AndroidKeyStore provider"
) {

    init {
        put(
            "KeyStore.AndroidKeyStore",  
            FakeKeyStore::class.java.name
        )
        put(
            "KeyGenerator.AES", 
            FakeKeyGenerator::class.java.name
        )
    }

    companion object {
        fun setup() {
            Security.addProvider(FakeAndroidKeyStoreProvider())
        }
    }
}