package com.yogeshpaliyal.keypass.ui.keypassRedux

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yogeshpaliyal.keypass.ui.redux.KeyPassRedux
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import com.yogeshpaliyal.keypass.ui.redux.states.generateDefaultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reduxkotlin.Store

val LocalState =
    compositionLocalOf { Store<KeyPassState>(KeyPassRedux.newReducer, generateDefaultState()) }

typealias Middleware<T> = (Store<T>) -> (suspend (action: Any, state: T) -> Unit)

typealias Reducer<T> = suspend (state: T, action: Any) -> T

class Store<T>(
    val reducer: Reducer<T>,
    val defaultState: T,
    val middlewares: List<Middleware<T>> = emptyList()
) {
    private val _uiState = mutableStateOf<T>(defaultState)
    val uiState: State<T>
        get() = _uiState

    fun updateState(newState: T) {
        _uiState.value = newState
    }

    fun dispatch(action: Any) {
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            for (function in middlewares) {

            }
            val newState = reducer(_uiState.value, action)
            updateState(newState)
        }
    }

}

@Composable
fun rememberTypedDispatcher(): (Any) -> Unit {
    val sture = LocalState.current
    val res by remember { sture.dispatch }
    return res
}