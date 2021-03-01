package com.yogeshpaliyal.keypass.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 26-12-2020 19:46
*/

@JvmName("IntentHelper")

fun Context.email(
    chooserTitle: String,
    email: String = "",
    subject: String = "",
    text: String = ""
): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")

    if (email.isNotEmpty())
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))

    if (subject.isNotEmpty())
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)

    if (text.isNotEmpty())
        intent.putExtra(Intent.EXTRA_TEXT, text)

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(Intent.createChooser(intent, chooserTitle))
        return true
    }
    return false
}

fun Context.makeCall(chooserTitle: String, number: String): Boolean {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("tel:$number")
        startActivity(Intent.createChooser(intent, chooserTitle))
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun Context.sendSMS(chooserTitle: String, number: String, text: String = ""): Boolean {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
        intent.putExtra("sms_body", text)
        startActivity(Intent.createChooser(intent, chooserTitle))
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun Context.share(chooserTitle: String, text: String): Boolean {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, chooserTitle))
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun Context.navigate(address: String) {
    val gmmIntentUri =
        Uri.parse("google.navigation:q=$address")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    mapIntent.resolveActivity(packageManager)?.let {
        startActivity(mapIntent)
    }
}
