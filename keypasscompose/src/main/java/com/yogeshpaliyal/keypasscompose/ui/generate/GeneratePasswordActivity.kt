package com.yogeshpaliyal.keypasscompose.ui.generate

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.yogeshpaliyal.keypasscompose.databinding.ActivityGeneratePasswordBinding
import com.yogeshpaliyal.keypasscompose.ui.generate.ui.GeneratePasswordScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneratePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGeneratePasswordBinding

    private val viewModel: GeneratePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneratePasswordBinding.inflate(layoutInflater)

        setContent {
            Mdc3Theme {
                GeneratePasswordScreen(viewModel)
            }
        }
    }
}
