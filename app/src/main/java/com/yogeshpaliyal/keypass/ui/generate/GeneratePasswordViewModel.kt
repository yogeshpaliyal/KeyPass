package com.yogeshpaliyal.keypass.ui.generate

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.data.PasswordConfig
import com.yogeshpaliyal.common.utils.PasswordGenerator
import com.yogeshpaliyal.common.utils.getUserSettings
import com.yogeshpaliyal.common.utils.setPasswordConfig
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

    private val _viewState = MutableStateFlow(PasswordConfig.Initial)
    val viewState = _viewState.asStateFlow()

    init {
        observeState(context)
    }

    fun retrieveSavedPasswordConfig(context: Context) {
        viewModelScope.launch {
            val passwordConfig = context.getUserSettings().passwordConfig
            _viewState.update {
                passwordConfig
            }
        }
    }


    fun generatePassword() {
        val currentViewState = _viewState.value

        val passwordGenerator = PasswordGenerator(
            currentViewState
        )

        _viewState.update {
            val newPassword = passwordGenerator.generatePassword()
            it.copy(password = newPassword)
        }
    }

    fun selectSymbolForPassword(symbol: Char){
        val tempList = _viewState.value.listOfSymbols.toMutableList()
        if(tempList.size == PasswordGenerator.totalSymbol.size && tempList.contains(symbol)){
            tempList.clear()
        }
        if(symbol == 's'){
            _viewState.update {
                it.copy(
                    listOfSymbols = PasswordGenerator.totalSymbol
                )
            }
            return
        }
        if(tempList.contains(symbol)){
            tempList.remove(symbol)
            _viewState.update {
                it.copy(
                    listOfSymbols = tempList.toList()
                )
            }
        }else{
            tempList.add(symbol)
            _viewState.update {
                it.copy(
                    listOfSymbols = tempList.toList()
                )
            }
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

    fun onBlankSpacesCheckedChange(checked: Boolean) {
        _viewState.update {
            it.copy(includeBlankSpaces = checked)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeState(context: Context) {
        viewModelScope.launch {
            // Save every changed value from password length with a delay
            _viewState
                .debounce(400)
                .collectLatest { state ->
                    context.setPasswordConfig(state)
                }
        }
    }
}
