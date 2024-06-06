package com.yogeshpaliyal.keypass.ui.changeDefaultPasswordLength

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.utils.getUserSettings
import com.yogeshpaliyal.common.utils.setDefaultPasswordLength
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeDefaultPasswordLengthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeDefaultPasswordLengthViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(ChangeDefaultPasswordLengthState())
    val viewState = _viewState.asStateFlow()

    /**
     * Initial [ChangeDefaultPasswordLengthState] with previously saved length
     * */
    fun retrieveSavedPasswordLength(context: Context) {
        viewModelScope.launch {
            _viewState.update {
                val oldPasswordLength =
                    context.getUserSettings().passwordConfig.length
                ChangeDefaultPasswordLengthState(length = oldPasswordLength)
            }
        }
    }

    /**
     * Stores the new password length from [_viewState] in the data store [Context.setKeyPassPasswordLength]
     * */
    fun updatePasswordLength(context: Context, callback: () -> Unit) {
        viewModelScope.launch {
            _viewState.value.length.let { length ->
                context.setDefaultPasswordLength(length)
                callback.invoke()
            }
        }
    }

    /**
     * Updates the state after changing the value of the slider
     * */
    fun onPasswordLengthChanged(value: Float) {
        _viewState.update {
            _viewState.value.copy(length = value)
        }
    }
}
