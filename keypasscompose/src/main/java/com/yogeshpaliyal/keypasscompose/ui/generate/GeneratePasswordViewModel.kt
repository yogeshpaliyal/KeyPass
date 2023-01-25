package com.yogeshpaliyal.keypasscompose.ui.generate

import androidx.lifecycle.ViewModel
import com.yogeshpaliyal.common.utils.PasswordGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GeneratePasswordViewModel @Inject constructor() : ViewModel() {

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun generatePassword(
        length: Int = 10,
        includeUppercaseLetters: Boolean = true,
        includeLowercaseLetters: Boolean = true,
        includeSymbols: Boolean = true,
        includeNumbers: Boolean = true,
    ) {
        val passwordGenerator = PasswordGenerator(
            length = length,
            includeUpperCaseLetters = includeUppercaseLetters,
            includeLowerCaseLetters = includeLowercaseLetters,
            includeSymbols = includeSymbols,
            includeNumbers = includeNumbers,
        )

        _password.value = passwordGenerator.generatePassword()
    }
}