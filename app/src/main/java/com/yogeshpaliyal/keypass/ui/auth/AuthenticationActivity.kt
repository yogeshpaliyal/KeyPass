package com.yogeshpaliyal.keypass.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.utils.getKeyPassPassword
import com.yogeshpaliyal.common.utils.setKeyPassPassword
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.nav.DashboardComposeActivity
import com.yogeshpaliyal.keypass.ui.style.KeyPassTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val AUTHENTICATION_RESULT = 707

sealed class AuthState(@StringRes val title: Int) {
    object CreatePassword : AuthState(R.string.create_password)
    class ConfirmPassword(val password: String) : AuthState(R.string.confirm_password)
    object Login : AuthState(R.string.login_to_enter_keypass)
}

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {

    @Preview(showSystemUi = true)
    @Composable
    fun AuthScreenPreview() {
        AuthScreen()
    }

    @Composable
    fun AuthScreen() {
        val context = LocalContext.current

        val coroutineScope = rememberCoroutineScope()

        val (state, setState) = remember {
            mutableStateOf<AuthState>(AuthState.Login)
        }

        val (password, setPassword) = remember(state) {
            mutableStateOf("")
        }

        val (passwordVisible, setPasswordVisible) = remember(state) { mutableStateOf(false) }

        val (passwordError, setPasswordError) = remember(state, password) {
            mutableStateOf<Int?>(null)
        }

        BackHandler(state is AuthState.ConfirmPassword) {
            setState(AuthState.CreatePassword)
        }

        LaunchedEffect(key1 = Unit, block = {
            coroutineScope.launch {
                val mPassword = context.getKeyPassPassword()
                if (mPassword == null) {
                    setState(AuthState.CreatePassword)
                }
            }
        })

        KeyPassTheme {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxSize(1f)
                    .verticalScroll(rememberScrollState()),
                Arrangement.SpaceEvenly,
                Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_undraw_unlock_24mb),
                    contentDescription = ""
                )

                Text(text = stringResource(id = state.title))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(1f),
                    value = password,
                    singleLine = true,
                    placeholder = {
                        Text(text = stringResource(id = R.string.enter_password))
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = setPassword,
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = passwordError),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        val image = if (passwordVisible) {
                            Icons.Rounded.Visibility
                        } else Icons.Rounded.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { setPasswordVisible(!passwordVisible) }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )

                Row(modifier = Modifier.fillMaxWidth(1f), Arrangement.SpaceEvenly) {
                    AnimatedVisibility(state is AuthState.ConfirmPassword) {
                        Button(onClick = {
                            setState(AuthState.CreatePassword)
                        }) {
                            Text(text = stringResource(id = R.string.back))
                        }
                    }

                    Button(onClick = {
                        when (state) {
                            is AuthState.CreatePassword -> {
                                if (password.isBlank()) {
                                    setPasswordError(R.string.enter_password)
                                } else {
                                    setState(AuthState.ConfirmPassword(password))
                                }
                            }

                            is AuthState.ConfirmPassword -> {
                                if (state.password == password) {
                                    coroutineScope.launch {
                                        context.setKeyPassPassword(password)
                                        onAuthComplete(context)
                                    }
                                } else {
                                    setPasswordError(R.string.password_no_match)
                                }
                            }

                            is AuthState.Login -> {
                                coroutineScope.launch {
                                    val savedPassword = context.getKeyPassPassword()
                                    if (savedPassword == password) {
                                        onAuthComplete(context)
                                    } else {
                                        setPasswordError(R.string.incorrect_password)
                                    }
                                }
                            }
                        }
                    }) {
                        Text(text = stringResource(id = R.string.str_continue))
                    }
                }
            }
        }
    }

    private fun onAuthComplete(context: Context) {
        // binding.passCodeView.isVisible = false
        val dashboardIntent = Intent(context, DashboardComposeActivity::class.java)
        startActivity(dashboardIntent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthScreen()
        }
    }
}
