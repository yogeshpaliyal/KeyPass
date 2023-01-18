package com.yogeshpaliyal.keypasscompose.ui.addTOTP

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yogeshpaliyal.common.constants.RequestCodes
import com.yogeshpaliyal.keypasscompose.databinding.ActivityScannerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerBinding

    private val REQUEST_CAM_PERMISSION = 432

    companion object {
        @JvmStatic
        fun start(activity: Activity) {
            val starter = Intent(activity, ScannerActivity::class.java)
            activity.startActivityForResult(starter, RequestCodes.SCANNER)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAM_PERMISSION) {
            if (isAllRequestGranted(grantResults)) {
                // codeScanner?.startPreview()
            }
        }
    }

    private fun isAllRequestGranted(grantResults: IntArray) =
        grantResults.all { it == PackageManager.PERMISSION_GRANTED }
}
