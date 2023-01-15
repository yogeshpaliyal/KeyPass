package com.yogeshpaliyal.keypasscompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.viewmodel.DashboardViewModel
import com.yogeshpaliyal.keypasscompose.ui.theme.KeyPassTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainComponent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainComponent() {
    KeyPassTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold(
                content = { innerPadding ->
                    Box(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()) {
                        AccountsListing()
                    }
                },
                bottomBar = {
                    MyMainBottomBar()
                }
            )
        }
    }
}


@Composable
fun AccountsListing() {
    val viewModel: DashboardViewModel = hiltViewModel<DashboardViewModel>()
    val list by viewModel.mediator.observeAsState()

    Toast.makeText(LocalContext.current, "List Print ${list?.value?.size}", Toast.LENGTH_SHORT).show()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (list?.value.isNullOrEmpty()) {
            item { 
                Text(text = "No Accounts found")
            }
        } else {
            items(list?.value ?: listOf()) { item ->
                if (item.type == AccountType.DEFAULT) {
                    Card(elevation = CardDefaults.elevatedCardElevation()) {
                        Text(text = item.username ?: "")
                    }
                } else {
                    Card(elevation = CardDefaults.elevatedCardElevation()) {
                        Text(text = "TOTP Item")
                    }
                }
            }
        }
    }

}

@Composable
fun MyMainBottomBar() {
    BottomAppBar(actions = {
        IconButton(onClick = { /* doSomething() */ }) {
            Icon(Icons.Filled.Menu, contentDescription = "Localized description")
        }
        IconButton(onClick = { /* doSomething() */ }) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Localized description",
            )
        }
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { /* do something */ },
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            Icon(Icons.Filled.Add, "Localized description")
        }
    })
}
