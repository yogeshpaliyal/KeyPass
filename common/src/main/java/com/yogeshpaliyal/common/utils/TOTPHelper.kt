package com.yogeshpaliyal.common.utils

import android.net.Uri
import org.apache.commons.codec.binary.Base32
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Locale

class TOTPHelper(uriString: String) {
    var secret: String?
    var label: String
    var issuer: String?

    init {
        var mUriString = uriString
        mUriString = mUriString.replaceFirst("otpauth".toRegex(), "http")
        val uri = Uri.parse(mUriString)
        val url = URL(mUriString)
        label = url.path.substring(1) // Remove leading slash `/`
        label = URLDecoder.decode(label, StandardCharsets.UTF_8.toString())
        if (url.protocol != "http") {
            throw Exception("Invalid Protocol")
        }
        if (url.host != "totp") {
            throw Exception("unknown otp type")
        }
        secret = uri.getQueryParameter("secret")
        if (secret == null) throw Exception("Empty secret")
        issuer = uri.getQueryParameter("issuer")
        if (issuer == null) {
            issuer = ""
        }
    }

    companion object {
        fun generate(secret: String?): String {
            return String.format(
                Locale.getDefault(),
                "%06d",
                generate(Base32().decode(secret?.uppercase()), System.currentTimeMillis() / 1000, 6)
            )
        }

        fun generate(key: ByteArray, t: Long, digits: Int): Int {
            return TokenCalculator.TOTP_RFC6238(
                key,
                30,
                t,
                digits,
                TokenCalculator.DEFAULT_ALGORITHM,
                0
            )
        }

        val progress: Long
            get() = 30 - System.currentTimeMillis() / 1000 % 30
    }
}
