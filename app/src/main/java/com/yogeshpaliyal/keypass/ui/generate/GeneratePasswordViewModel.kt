package com.yogeshpaliyal.keypass.ui.generate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.utils.PasswordGenerator
import com.yogeshpaliyal.common.utils.getUserSettings
import com.yogeshpaliyal.common.utils.setDefaultPasswordLength
import com.yogeshpaliyal.keypass.ui.generate.ui.components.DEFAULT_PASSWORD_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratePasswordViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val _viewState = MutableStateFlow(GeneratePasswordViewState.Initial)
    val viewState = _viewState.asStateFlow()

    init {
        observeState(context)
    }

    fun retrieveSavedPasswordLength(context: Context) {
        viewModelScope.launch {
            val savedPasswordLength = context.getUserSettings().defaultPasswordLength ?: DEFAULT_PASSWORD_LENGTH
            _viewState.update {
                _viewState.value.copy(length = savedPasswordLength)
            }
        }
    }

    fun generatePassword() {
        val currentViewState = _viewState.value

        val passwordGenerator = PasswordGenerator(
            length = currentViewState.length.toInt(),
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
            it.copy(length = value)
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

    @OptIn(FlowPreview::class)
    private fun observeState(context: Context) {
        viewModelScope.launch {
            // Save every changed value from password length with a delay
            _viewState
                .debounce(400)
                .collectLatest { state ->
                    context.setDefaultPasswordLength(state.length)
                }
        }
    }
}
