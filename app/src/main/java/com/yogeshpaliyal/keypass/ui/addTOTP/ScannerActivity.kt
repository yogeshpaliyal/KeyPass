package com.yogeshpaliyal.keypass.ui.addTOTP

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yogeshpaliyal.keypass.constants.IntentKeys
import com.yogeshpaliyal.keypass.constants.RequestCodes
import com.yogeshpaliyal.keypass.databinding.ActivityScannerBinding
import com.yogeshpaliyal.keypass.utils.TOTPHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerBinding

    private var codeScanner: CodeScanner? = null

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

        codeScanner = CodeScanner(this, binding.scannerView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAM_PERMISSION)
        } else {
            codeScanner?.startPreview()
            // Callbacks
            codeScanner?.decodeCallback = DecodeCallback { result ->
                runOnUiThread {

                    try {
                        val secretKey = TOTPHelper.getSecretKey(result.text)
                        setResult(RESULT_OK, Intent().also {
                            it.putExtra(IntentKeys.SCANNED_TEXT, secretKey)
                        })
                        finish()
                    } catch (e: Exception) {
                        MaterialAlertDialogBuilder(this)
                            .setMessage(e.localizedMessage)
                            .show()
                        e.printStackTrace()
                    }


                    //Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                }
            }
            codeScanner?.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                runOnUiThread {
                    /* Toast.makeText(
                         this, "Camera initialization error: ${it.message}",
                         Toast.LENGTH_LONG
                     ).show()*/
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner?.startPreview()
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAM_PERMISSION) {
            if (isAllRequestGranted(grantResults)) {
                codeScanner?.startPreview()
            }
        }
    }


    private fun isAllRequestGranted(grantResults: IntArray) =
        grantResults.all { it == PackageManager.PERMISSION_GRANTED }

}