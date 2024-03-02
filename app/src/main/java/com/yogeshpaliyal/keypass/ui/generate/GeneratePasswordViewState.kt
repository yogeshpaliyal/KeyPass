package com.yogeshpaliyal.keypass.ui.generate

import com.yogeshpaliyal.keypass.ui.generate.ui.components.DEFAULT_PASSWORD_LENGTH

data class GeneratePasswordViewState(
    val length: Float,
    val includeUppercaseLetters: Boolean,
    val includeLowercaseLetters: Boolean,
    val includeSymbols: Boolean,
    val includeNumbers: Boolean,
    val includeBlankSpaces: Boolean,
    val password: String
) {
    companion object {
        val Initial = GeneratePasswordViewState(
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
