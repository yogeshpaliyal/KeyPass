package com.yogeshpaliyal.keypass.ui.nav

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yogeshpaliyal.keypass.databinding.LayoutMySettingsFragmentBinding
import com.yogeshpaliyal.keypass.ui.detail.DetailActivity
import com.yogeshpaliyal.keypass.ui.home.DashboardViewModel
import com.yogeshpaliyal.keypass.ui.home.Main
import com.yogeshpaliyal.keypass.ui.redux.Action
import com.yogeshpaliyal.keypass.ui.redux.BottomSheetAction
import com.yogeshpaliyal.keypass.ui.redux.Home
import com.yogeshpaliyal.keypass.ui.redux.KeyPassRedux
import com.yogeshpaliyal.keypass.ui.redux.ScreeNavigationAction
import com.yogeshpaliyal.keypass.ui.redux.ScreenRoutes
import com.yogeshpaliyal.keypass.ui.redux.UpdateContextAction
import com.yogeshpaliyal.keypass.ui.redux.UpdateNavControllerAction
import com.yogeshpaliyal.keypass.ui.style.KeyPassTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class DashboardComposeActivity : AppCompatActivity() {

    private val mViewModel by viewModels<DashboardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setContent {
            Dashboard(viewModel = mViewModel)
        }
    }
}

@Composable
fun Dashboard(viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val (appState, updateAppState) = remember { mutableStateOf(KeyPassRedux.getCurrentState()) }

    val navController = rememberNavController()
    val context = LocalContext.current

    DisposableEffect(KeyPassRedux, navController, context) {
        KeyPassRedux.dispatchAction(UpdateContextAction(context))
        KeyPassRedux.dispatchAction(UpdateNavControllerAction(navController))

        val unsubscribe = KeyPassRedux.subscribeToStore {
            updateAppState(KeyPassRedux.getCurrentState())
        }

        onDispose {
            KeyPassRedux.dispatchAction(UpdateContextAction(null))
            KeyPassRedux.dispatchAction(UpdateNavControllerAction(null))
            unsubscribe()
        }
    }

    KeyPassTheme {
        Scaffold(bottomBar = {
            KeyPassBottomBar(navController) {
                KeyPassRedux.dispatchAction(it)
            }
        }) { paddingValues ->
            Surface(modifier = Modifier.padding(paddingValues)) {
                NavHost(
                    navController = navController,
                    startDestination = ScreenRoutes.HOME
                ) {
                    composable(
                        ScreenRoutes.HOME,
                        arguments = listOf(
                            navArgument("tag") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = ""
                            }
                        )
                    ) { backStackEntry ->
                        val type = backStackEntry.arguments?.getString("tag")
                        Main(viewModel, type)
                    }
                    composable(ScreenRoutes.SETTINGS) {
                        MySettings()
                    }
                }

                if (appState.bottomSheet.isBottomSheetOpen) {
                    OptionBottomBar({
                        KeyPassRedux.dispatchAction(it)
                        if (it !is BottomSheetAction) {
                            KeyPassRedux.dispatchAction(BottomSheetAction.HomeNavigationMenu(false))
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun MySettings() {
    AndroidViewBinding(LayoutMySettingsFragmentBinding::inflate) {
    }
}

@Composable
fun OptionBottomBar(
    dispatchAction: (Action) -> Unit,
    viewModel: BottomNavViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val navigationItems by viewModel.navigationList.observeAsState()
    ModalBottomSheet(onDismissRequest = {
        dispatchAction(BottomSheetAction.HomeNavigationMenu(false))
    }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(32.dp)
        ) {
            if (navigationItems != null) {
                items(navigationItems!!) {
                    when (it) {
                        is NavigationModelItem.NavDivider -> {
                            NavItemSection(it)
                        }

                        is NavigationModelItem.NavEmailFolder -> {
                            NavMenuFolder(folder = it) {
                                dispatchAction(ScreeNavigationAction.Home(it.category))
                            }
                        }

                        is NavigationModelItem.NavMenuItem -> {
                            NavItem(it) {
                                dispatchAction(it.action)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavMenuFolder(folder: NavigationModelItem.NavEmailFolder, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            )

    ) {
        Text(
            text = folder.category,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun NavItem(item: NavigationModelItem.NavMenuItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            )
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = stringResource(id = item.titleRes),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun NavItemSection(divider: NavigationModelItem.NavDivider) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Divider()
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = divider.title.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelMedium,
            fontSize = TextUnit(12f, TextUnitType.Sp)
        )
    }
}

@Composable
fun KeyPassBottomBar(navController: NavController, onActionItemClick: (Action) -> Unit) {
    val context = LocalContext.current

    BottomAppBar(actions = {
        IconButton(onClick = {
            onActionItemClick(BottomSheetAction.HomeNavigationMenu(true))
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Rounded.Menu),
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(onClick = {
            onActionItemClick(ScreeNavigationAction.Settings())
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Rounded.Settings),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }, floatingActionButton = {
            FloatingActionButton(onClick = {
                DetailActivity.start(context)
            }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Add),
                    contentDescription = "Add"
                )
            }
        })
}
