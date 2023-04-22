package com.yogeshpaliyal.keypass.ui.generate

data class GeneratePasswordViewState(
    val length: Int,
    val includeUppercaseLetters: Boolean,
    val includeLowercaseLetters: Boolean,
    val includeSymbols: Boolean,
    val includeNumbers: Boolean,
    val password: String
) {
    companion object {
        val Initial = GeneratePasswordViewState(
            length = 10,
            includeUppercaseLetters = true,
            includeLowercaseLetters = true,
            includeSymbols = true,
            includeNumbers = true,
            password = ""
        )
    }
}
