package com.yogeshpaliyal.keypass.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.zxing.integration.android.IntentIntegrator
import com.yogeshpaliyal.common.constants.ScannerType
import com.yogeshpaliyal.keypass.MyApplication

class QRScanner : ActivityResultContract<Int, QRScannerResult>() {

    @ScannerType
    var scannerType: Int = ScannerType.Default

    override fun createIntent(context: Context, input: Int): Intent {
        scannerType = input
        val intentIntegration = if (context is Activity) {
            IntentIntegrator(context)
        } else {
            null
        }
        (context.applicationContext as? MyApplication)?.knownActivityLaunchTriggered()
        return intentIntegration?.setPrompt("")?.createScanIntent() ?: Intent()
    }

    override fun parseResult(resultCode: Int, result: Intent?): QRScannerResult {
        val textResult = IntentIntegrator.parseActivityResult(
            IntentIntegrator.REQUEST_CODE,
            resultCode,
            result
        )?.contents
        return QRScannerResult(scannerType, textResult)
    }
}
