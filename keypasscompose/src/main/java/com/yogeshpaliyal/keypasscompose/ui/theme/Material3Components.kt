package com.yogeshpaliyal.keypasscompose.ui.theme

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp


@Composable
fun Material3BottomAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    cutoutShape: Shape? = null,
    elevation: Dp = AppBarDefaults.BottomAppBarElevation,
    contentPadding: PaddingValues = AppBarDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    BottomAppBar(
        modifier,
        backgroundColor,
        contentColor,
        cutoutShape,
        elevation,
        contentPadding,
        content
    )
}

@Composable
fun Material3Scaffold(modifier: Modifier = Modifier,
                      scaffoldState: ScaffoldState = rememberScaffoldState(),
                      topBar: @Composable () -> Unit = {},
                      bottomBar: @Composable () -> Unit = {},
                      snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
                      floatingActionButton: @Composable () -> Unit = {},
                      floatingActionButtonPosition: FabPosition = FabPosition.End,
                      isFloatingActionButtonDocked: Boolean = false,
                      drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
                      drawerGesturesEnabled: Boolean = true,
                      drawerShape: Shape = androidx.compose.material.MaterialTheme.shapes.large,
                      drawerElevation: Dp = DrawerDefaults.Elevation,
                      drawerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
                      drawerContentColor: Color = contentColorFor(
                          drawerBackgroundColor
                      ),
                      drawerScrimColor: Color = DrawerDefaults.scrimColor,
                      backgroundColor: Color = MaterialTheme.colorScheme.background,
                      contentColor: Color = contentColorFor(
                          backgroundColor
                      ),
                      content: @Composable (PaddingValues) -> Unit){
    Scaffold(modifier, scaffoldState, topBar, bottomBar, snackbarHost, floatingActionButton, floatingActionButtonPosition, isFloatingActionButtonDocked, drawerContent, drawerGesturesEnabled, drawerShape, drawerElevation, drawerBackgroundColor, drawerContentColor, drawerScrimColor, backgroundColor, contentColor, content)
}