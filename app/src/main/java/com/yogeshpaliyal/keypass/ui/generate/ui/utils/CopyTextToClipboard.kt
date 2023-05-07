package com.yogeshpaliyal.keypass.ui.generate.ui.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

fun copyTextToClipboard(
    context: Context,
    text: String,
    label: String
) {
    val clipboard =
        context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}
