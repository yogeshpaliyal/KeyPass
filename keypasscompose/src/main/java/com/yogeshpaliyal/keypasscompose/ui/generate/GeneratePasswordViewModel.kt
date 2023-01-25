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

    private val _viewState  = MutableStateFlow(GeneratePasswordViewState.Initial)
    val viewState = _viewState.asStateFlow()

    fun generatePassword() {
        val currentViewState = _viewState.value

        val passwordGenerator = PasswordGenerator(
            length = currentViewState.length,
            includeUpperCaseLetters = currentViewState.includeUppercaseLetters,
            includeLowerCaseLetters = currentViewState.includeLowercaseLetters,
            includeSymbols = currentViewState.includeSymbols,
            includeNumbers = currentViewState.includeNumbers,
        )

        _password.value = passwordGenerator.generatePassword()
    }
}