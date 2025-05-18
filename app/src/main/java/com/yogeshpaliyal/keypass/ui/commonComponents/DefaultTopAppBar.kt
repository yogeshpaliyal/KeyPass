package com.yogeshpaliyal.keypass.ui.commonComponents

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import org.reduxkotlin.compose.rememberTypedDispatcher

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DefaultTopAppBar(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    extraAction: (@Composable RowScope.() -> Unit)? = null,
    title: Int,
    subtitle: Int? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val dispatchAction = rememberTypedDispatcher<Action>()

    LargeFlexibleTopAppBar(modifier = modifier, scrollBehavior = scrollBehavior, subtitle = {
        if (subtitle != null) {
            Text(
                text = stringResource(id = subtitle),
            )
        }
    }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Unspecified, scrolledContainerColor = MaterialTheme.colorScheme.surface),
        title = {
        Text(
            text = stringResource(id = title),
        )
    }, actions = {
        extraAction?.invoke(this)
    }, navigationIcon = {
        if (showBackButton) {
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
    })
}
