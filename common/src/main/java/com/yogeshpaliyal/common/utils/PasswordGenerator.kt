package com.yogeshpaliyal.common.utils

class PasswordGenerator(
    private var length: Int,
    private var includeUpperCaseLetters: Boolean,
    private var includeLowerCaseLetters: Boolean,
    private var includeSymbols: Boolean,
    private var includeNumbers: Boolean
) {

    constructor() : this(10, true, true, true, true)

    private val UPPER_CASE = 0
    private val LOWER_CASE = 1
    private val NUMBERS = 2
    private val SYMBOLS = 3

    fun generatePassword(): String {
        var password = ""
        val list = ArrayList<Int>()
        if (includeUpperCaseLetters) {
            list.add(UPPER_CASE)
        }
        if (includeLowerCaseLetters) {
            list.add(LOWER_CASE)
        }
        if (includeNumbers) {
            list.add(NUMBERS)
        }
        if (includeSymbols) {
            list.add(SYMBOLS)
        }

        for (i in 1..length) {
            if (list.isNotEmpty()) {
                when (list.random()) {
                    UPPER_CASE -> password += ('A'..'Z').random().toString()
                    LOWER_CASE -> password += ('a'..'z').random().toString()
                    NUMBERS -> password += ('0'..'9').random().toString()
                    SYMBOLS -> password += listOf('!', '@', '#', '$', '%', '&', '*', '+', '=', '-', '~', '?', '/', '_').random().toString()
                }
            }
        }
        return password
    }
}
