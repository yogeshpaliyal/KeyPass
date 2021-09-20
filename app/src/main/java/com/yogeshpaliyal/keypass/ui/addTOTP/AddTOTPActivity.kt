package com.yogeshpaliyal.keypass.ui.addTOTP

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.databinding.ActivityAddTotpactivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTOTPActivity : AppCompatActivity() {

    companion object{
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AddTOTPActivity::class.java)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityAddTotpactivityBinding

    private val mViewModel by viewModels<AddTOTPViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTotpactivityBinding.inflate(layoutInflater)
        binding.mViewModel = mViewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.tilSecretKey.setEndIconOnClickListener {
            ScannerActivity.start(this)
        }

    }
}