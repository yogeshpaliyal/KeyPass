package com.yogeshpaliyal.keypasscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.yogeshpaliyal.keypasscompose.ui.theme.KeyPassTheme
import com.yogeshpaliyal.keypasscompose.ui.theme.Material3BottomAppBar
import com.yogeshpaliyal.keypasscompose.ui.theme.Material3Scaffold

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeyPassTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    Material3Scaffold(bottomBar = {
                        Material3BottomAppBar(cutoutShape = RoundedCornerShape(50)) {
                            IconButton(
                                onClick = {
                                    /* doSomething() */
                                }
                            ) {
                                Icon(Icons.Filled.Menu,"")
                            }

                            Spacer(Modifier.weight(1f, true))
                            IconButton(
                                onClick = {
                                    /* doSomething() */
                                }
                            ) {
                                Icon(Icons.Filled.Star,"")
                            }
                        }
                    }, floatingActionButton = {
                        FloatingActionButton(
                            onClick = {

                            },
                            contentColor = Color.White,
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(Icons.Filled.Add,"")
                        }
                    }, floatingActionButtonPosition = FabPosition.Center,
                    isFloatingActionButtonDocked = true) {

                    }
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KeyPassTheme {
        Greeting("Android")
    }
}