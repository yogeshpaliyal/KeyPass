package com.yogeshpaliyal.keypass.ui.commonComponents

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun DefaultBottomAppBar(extraAction: (@Composable RowScope.() -> Unit)? = null) {
    val dispatchAction = rememberTypedDispatcher<Action>()

    BottomAppBar {
        extraAction?.invoke(this)
        IconButton(onClick = {
            dispatchAction(GoBackAction)
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Rounded.ArrowBackIosNew),
                contentDescription = "Go Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
