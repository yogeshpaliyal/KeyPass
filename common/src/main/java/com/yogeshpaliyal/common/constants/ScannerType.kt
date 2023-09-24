package com.yogeshpaliyal.common.constants

annotation class ScannerType {
    companion object {
        const val Default = 0
        const val Password = 1 // used to store password and user information
        const val Secret = 2 // Secret key for TOTP
    }
}
