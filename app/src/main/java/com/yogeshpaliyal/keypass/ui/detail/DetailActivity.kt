package com.yogeshpaliyal.keypass.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.databinding.FragmentDetailBinding
import com.yogeshpaliyal.keypass.utils.PasswordGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 10:38
*/
@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    lateinit var binding: FragmentDetailBinding


    @Inject
    lateinit var appDb: AppDatabase

    companion object {

        private const val ARG_ACCOUNT_ID = "ARG_ACCOUNT_ID"

        @JvmStatic
        fun start(context: Context?, accountId: Long? = null) {
            val starter = Intent(context, DetailActivity::class.java)
                .putExtra(ARG_ACCOUNT_ID, accountId)
            context?.startActivity(starter)
        }
    }

    private val mViewModel by viewModels<DetailViewModel>()


    private val accountId by lazy {
        intent?.extras?.getLong(ARG_ACCOUNT_ID) ?: -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        mViewModel.loadAccount(accountId)
        mViewModel.accountModel.observe(this, Observer {
            binding.accountData = it
        })

        if (accountId > 0) {
            binding.bottomAppBar.replaceMenu(R.menu.bottom_app_bar_detail)

            binding.tilPassword.startIconDrawable = null
        } else {
            binding.tilPassword.setStartIconDrawable(R.drawable.ic_round_refresh_24)

            binding.tilPassword.setStartIconOnClickListener {
                binding.etPassword.setText(PasswordGenerator().generatePassword())
            }
        }



        binding.bottomAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_delete) {
                deleteAccount()
                return@setOnMenuItemClickListener true
            }

            return@setOnMenuItemClickListener false
        }


        binding.btnSave.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val model = mViewModel.accountModel.value
                if (model != null) {
                    appDb.getDao().insertOrUpdateAccount(model)
                }
                withContext(Dispatchers.Main) {
                    onBackPressed()
                }
            }
        }
    }

    private fun deleteAccount() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_account_title))
            .setMessage(getString(R.string.delete_account_msg))
            .setPositiveButton(
                getString(R.string.delete)
            ) { dialog, which ->
                dialog?.dismiss()
                lifecycleScope.launch(Dispatchers.IO) {
                    if (accountId > 0L) {
                        appDb.getDao().deleteAccount(accountId)
                    }
                    withContext(Dispatchers.Main) {
                        onBackPressed()
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }.show()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_app_bar_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

}