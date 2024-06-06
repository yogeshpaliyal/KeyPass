package com.yogeshpaliyal.common.data

import androidx.annotation.Keep
import com.yogeshpaliyal.common.utils.PasswordGenerator
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PasswordConfig(
    val length: Float,
    val includeUppercaseLetters: Boolean,
    val includeLowercaseLetters: Boolean,
    val includeSymbols: Boolean,
    val listOfSymbols: List<Char>,
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
            listOfSymbols = PasswordGenerator.totalSymbol,
            includeNumbers = true,
            includeBlankSpaces = true,
            password = ""
        )
    }
}
