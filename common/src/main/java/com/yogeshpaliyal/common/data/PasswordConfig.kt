package com.yogeshpaliyal.common.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PasswordConfig(
    val length: Float,
    val includeUppercaseLetters: Boolean,
    val includeLowercaseLetters: Boolean,
    val includeSymbols: Boolean,
    val includeNumbers: Boolean,
    val includeBlankSpaces: Boolean,
    val password: String
) {
    companion object {
        val Initial = PasswordConfig(
            length = DEFAULT_PASSWORD_LENGTH,
            includeUppercaseLetters = true,
            includeLowercaseLetters = true,
            includeSymbols = true,
            includeNumbers = true,
            includeBlankSpaces = true,
            password = ""
        )
    }
}
