package com.yogeshpaliyal.common.utils;

import android.net.Uri;

import org.apache.commons.codec.binary.Base32;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.net.URLDecoder;

public class TOTPHelper {
    public String secret;

    public String label;

    public String issuer;

    public static String generate(String secret) {
        return String.format(Locale.getDefault(), "%06d", generate(new Base32().decode(secret.toUpperCase(Locale.ENGLISH)), System.currentTimeMillis() / 1000, 6));
    }

    public static int generate(byte[] key, long t, int digits) {
        return TokenCalculator.TOTP_RFC6238(key, 30, t, digits, TokenCalculator.DEFAULT_ALGORITHM, 0);
    }

    public static long getProgress() {
        return 30 - ((System.currentTimeMillis() / 1000) % 30);
    }

    public TOTPHelper(String uriString) throws Exception {
        uriString = uriString.replaceFirst("otpauth", "http");
        Uri uri = Uri.parse(uriString);
        URL url = new URL(uriString);

        label = url.getPath().substring(1); // Remove leading slash `/`
        label = URLDecoder.decode(label, StandardCharsets.UTF_8.toString());

        if (!url.getProtocol().equals("http")) {
            throw new Exception("Invalid Protocol");
        }

        if (!url.getHost().equals("totp")) {
            throw new Exception("unknown otp type");
        }

        secret = uri.getQueryParameter("secret");

        if (secret == null)
            throw new Exception("Empty secret");

        issuer = uri.getQueryParameter("issuer");

        if (issuer == null) {
            issuer = "";
        }
    }
}