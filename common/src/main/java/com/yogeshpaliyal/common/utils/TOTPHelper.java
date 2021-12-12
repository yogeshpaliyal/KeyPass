package com.yogeshpaliyal.common.utils;

import android.net.Uri;

import org.apache.commons.codec.binary.Base32;

import java.net.URL;
import java.util.Locale;

public class TOTPHelper {
    public static final String SHA1 = "HmacSHA1";

    public static String generate(String secret) {
        return String.format(Locale.getDefault(), "%06d", generate(new Base32().decode(secret.toUpperCase()), System.currentTimeMillis() / 1000, 6));
    }

    public static long getProgress() {
        return 30 - ((System.currentTimeMillis() / 1000) % 30);
    }


    public static String getSecretKey(String contents) throws Exception {

        contents = contents.replaceFirst("otpauth", "http");
        Uri uri = Uri.parse(contents);
        URL url = new URL(contents);

        if (!url.getProtocol().equals("http")) {
            throw new Exception("Invalid Protocol");
        }


        if (!url.getHost().equals("totp")) {
            throw new Exception("unknown otp type");
        }

        String secret = uri.getQueryParameter("secret");

        if (secret == null)
            throw new Exception("Empty secret");


        return secret;
    }

    public static int generate(byte[] key, long t, int digits) {
        return TokenCalculator.TOTP_RFC6238(key, 30, t, digits, TokenCalculator.DEFAULT_ALGORITHM, 0);
    }

    /**
     * Returns the label with issuer prefix removed (if present)
     *
     * @param issuer - Name of the issuer to remove from the label
     * @param label  - Full label from which the issuer should be removed
     * @return - label with the issuer removed
     */
    private static String getStrippedLabel(String issuer, String label) {
        if (issuer == null || issuer.isEmpty() || !label.startsWith(issuer + ":")) {
            return label.trim();
        } else {
            return label.substring(issuer.length() + 1).trim();
        }
    }

}