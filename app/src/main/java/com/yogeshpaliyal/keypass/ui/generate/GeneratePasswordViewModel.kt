package com.yogeshpaliyal.keypass.ui.generate

import androidx.lifecycle.ViewModel
import com.yogeshpaliyal.common.utils.PasswordGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GeneratePasswordViewModel @Inject constructor() : ViewModel() {

    private val _viewState = MutableStateFlow(GeneratePasswordViewState.Initial)
    val viewState = _viewState.asStateFlow()

    fun generatePassword() {
        val currentViewState = _viewState.value

        val passwordGenerator = PasswordGenerator(
            length = currentViewState.length,
            includeUpperCaseLetters = currentViewState.includeUppercaseLetters,
            includeLowerCaseLetters = currentViewState.includeLowercaseLetters,
            includeSymbols = currentViewState.includeSymbols,
            includeNumbers = currentViewState.includeNumbers
        )

        _viewState.update {
            val newPassword = passwordGenerator.generatePassword()
            it.copy(password = newPassword)
        }
    }

    fun onPasswordLengthSliderChange(value: Float) {
        _viewState.update {
            it.copy(length = value.toInt())
        }
    }

    fun onUppercaseCheckedChange(checked: Boolean) {
        _viewState.update {
            it.copy(includeUppercaseLetters = checked)
        }
    }

    fun onLowercaseCheckedChange(checked: Boolean) {
        _viewState.update {
            it.copy(includeLowercaseLetters = checked)
        }
    }

    fun onNumbersCheckedChange(checked: Boolean) {
        _viewState.update {
            it.copy(includeNumbers = checked)
        }
    }

    fun onSymbolsCheckedChange(checked: Boolean) {
        _viewState.update {
            it.copy(includeSymbols = checked)
        }
    }
}
