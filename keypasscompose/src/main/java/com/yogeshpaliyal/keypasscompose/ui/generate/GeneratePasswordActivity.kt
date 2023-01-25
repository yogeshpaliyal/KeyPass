package com.yogeshpaliyal.keypasscompose.ui.generate

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.yogeshpaliyal.keypasscompose.R
import com.yogeshpaliyal.keypasscompose.databinding.ActivityGeneratePasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GeneratePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGeneratePasswordBinding

    private val viewModel: GeneratePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneratePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.generatePassword()

        binding.btnRefresh.setOnClickListener {
            viewModel.generatePassword()
        }

        addOnFormChangeListeners()
        collectStateFlows()

        binding.tilPassword.setEndIconOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("random_password", binding.etPassword.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOnFormChangeListeners() {
        onLengthSliderValueChange()
        onUppercaseCheckedChange()
        onLowercaseCheckedChange()
        onNumberCheckedChange()
        onSymbolCheckedChange()
    }

    private fun onLengthSliderValueChange() {
        binding.sliderPasswordLength.addOnChangeListener { _, value, _ ->
            viewModel.onPasswordLengthSliderChange(value)
        }
    }

    private fun onUppercaseCheckedChange() {
        binding.cbCapAlphabets.setOnCheckedChangeListener { _, checked ->
            viewModel.onUppercaseCheckedChange(checked)
        }
    }

    private fun onLowercaseCheckedChange() {
        binding.cbLowerAlphabets.setOnCheckedChangeListener { _, checked ->
            viewModel.onLowercaseCheckedChange(checked)
        }
    }

    private fun onNumberCheckedChange() {
        binding.cbNumbers.setOnCheckedChangeListener { _, checked ->
            viewModel.onNumbersCheckedChange(checked)
        }
    }

    private fun onSymbolCheckedChange() {
        binding.cbSymbols.setOnCheckedChangeListener { _, checked ->
            viewModel.onSymbolsCheckedChange(checked)
        }
    }

    private fun collectStateFlows() {
        collectPassword()
        collectViewState()
    }

    private fun collectPassword() {
        lifecycleScope.launch {
            viewModel.password
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    updatePasswordEditText(it)
                }
        }
    }

    private fun collectViewState() {
        lifecycleScope.launch {
            viewModel.viewState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    updatePasswordEditText(it.password)
                }
        }
    }

    private fun updatePasswordEditText(password: String) {
        binding.etPassword.apply {
            setText(password)
            setSelection(password.length)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.sliderPasswordLength.clearOnChangeListeners()
    }
}
