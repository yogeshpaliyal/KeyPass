package com.yogeshpaliyal.keypass.ui.addTOTP

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.yogeshpaliyal.common.utils.TOTPHelper
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.databinding.ActivityAddTotpactivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTOTPActivity : AppCompatActivity() {

    companion object {

        private const val ARG_ACCOUNT_ID = "account_id"

        @JvmStatic
        fun start(context: Context?, accountId: String? = null) {

            val starter = Intent(context, AddTOTPActivity::class.java)
            starter.putExtra(ARG_ACCOUNT_ID, accountId)
            context?.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityAddTotpactivityBinding

    private val mViewModel by viewModels<AddTOTPViewModel>()

    private val accountId by lazy {
        intent.extras?.getString(ARG_ACCOUNT_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTotpactivityBinding.inflate(layoutInflater)
        binding.mViewModel = mViewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.tilSecretKey.isVisible = accountId == null
        mViewModel.loadOldAccount(accountId)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.tilSecretKey.setEndIconOnClickListener {
            // ScannerActivity.start(this)
            IntentIntegrator(this).setPrompt("").initiateScan()
        }

        mViewModel.error.observe(
            this,
            Observer {
                it?.getContentIfNotHandled()?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        )

        mViewModel.goBack.observe(
            this,
            Observer {
                it.getContentIfNotHandled()?.let {
                    onBackPressed()
                }
            }
        )

        binding.btnSave.setOnClickListener {
            mViewModel.saveAccount(accountId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (accountId != null)
            menuInflater.inflate(R.menu.menu_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            deleteAccount()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAccount() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_account_title))
            .setMessage(getString(R.string.delete_account_msg))
            .setPositiveButton(
                getString(R.string.delete)
            ) { dialog, which ->
                dialog?.dismiss()

                if (accountId != null) {
                    mViewModel.deleteAccount(accountId!!) {
                        onBackPressed()
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents != null) {
                try {
                    val totp = TOTPHelper(result.contents)
                    mViewModel.setSecretKey(totp.secret)
                    mViewModel.setAccountName(totp.label)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
