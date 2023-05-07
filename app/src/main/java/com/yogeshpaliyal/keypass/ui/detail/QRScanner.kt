package com.yogeshpaliyal.keypass.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.zxing.integration.android.IntentIntegrator

class QRScanner : ActivityResultContract<Void?, String?>() {
    override fun createIntent(context: Context, input: Void?): Intent {
        val intentIntegration = if (context is Activity) {
            IntentIntegrator(context)
        } else {
            null
        }
        return intentIntegration?.setPrompt("")?.createScanIntent() ?: Intent()
    }

    override fun parseResult(resultCode: Int, result: Intent?): String? {
        return IntentIntegrator.parseActivityResult(
            IntentIntegrator.REQUEST_CODE,
            resultCode,
            result
        )?.contents
    }
}
