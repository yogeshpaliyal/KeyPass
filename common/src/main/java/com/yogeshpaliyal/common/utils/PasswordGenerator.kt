package com.yogeshpaliyal.common.utils

import com.yogeshpaliyal.common.data.PasswordConfig

class PasswordGenerator(
    val passwordConfig: PasswordConfig
) {
    private val UPPER_CASE = 0
    private val LOWER_CASE = 1
    private val NUMBERS = 2
    private val SYMBOLS = 3
    private val BLANKSPACES = 4

    companion object {
        val totalSymbol = listOf('!', '@', '#', '$', '%', '&', '*', '+', '=', '-', '~', '?', '/', '_')
    }

    fun generatePassword(): String {
        var password = ""
        val list = ArrayList<Int>()
        if (passwordConfig.includeUppercaseLetters) {
            list.add(UPPER_CASE)
        }
        if (passwordConfig.includeLowercaseLetters) {
            list.add(LOWER_CASE)
        }
        if (passwordConfig.includeNumbers) {
            list.add(NUMBERS)
        }
        if (passwordConfig.includeSymbols) {
            list.add(SYMBOLS)
        }

        if (passwordConfig.includeBlankSpaces) {
            list.add(BLANKSPACES)
        }

        for (i in 1..passwordConfig.length.toInt()) {
            if (list.isNotEmpty()) {
                when (list.random()) {
                    UPPER_CASE -> password += ('A'..'Z').random().toString()
                    LOWER_CASE -> password += ('a'..'z').random().toString()
                    NUMBERS -> password += ('0'..'9').random().toString()
                    SYMBOLS -> password += passwordConfig.listOfSymbols.random().toString()
                    BLANKSPACES -> password += (' ').toString()
                }
            }
        }
        return password
    }
}
